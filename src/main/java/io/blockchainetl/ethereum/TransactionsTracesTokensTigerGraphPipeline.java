package io.blockchainetl.ethereum;

import com.google.api.client.util.Lists;
import io.blockchainetl.bitcoin.domain.Transaction;
import io.blockchainetl.bitcoin.domain.TransactionInput;
import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import io.blockchainetl.common.utils.CryptoCompare;
import io.blockchainetl.common.utils.JsonUtils;
import io.blockchainetl.common.utils.StringUtils;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.state.*;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.values.KV;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

import static io.blockchainetl.common.tigergraph.Utils.tigerGraphPost;
import static org.apache.beam.vendor.guava.v26_0_jre.com.google.common.base.MoreObjects.firstNonNull;


public class TransactionsTracesTokensTigerGraphPipeline {

    private static final Logger LOG =
            LoggerFactory.getLogger(TransactionsTracesTokensTigerGraphPipeline.class);

    public static void runPipeline(
            PubSubToClickhousePipelineOptions options,
            ChainConfig chainConfig,
            String chain,
            String currencyCode,
            String[] tigergraphHosts
    ) {
        Pipeline p = Pipeline.create(options);

        buildTransactionPipeline(p, options, chainConfig, chain, currencyCode, tigergraphHosts);

        PipelineResult pipelineResult = p.run();
        LOG.info(pipelineResult.toString());
    }



    public static void buildTransactionPipeline(Pipeline p,
                                                PubSubToClickhousePipelineOptions options,
                                                ChainConfig chainConfig,
                                                String chain,
                                                String currencyCode,
                                                String[] tigergraphHosts) {
        String transformNameSuffix =
                StringUtils.capitalizeFirstLetter(chainConfig.getTransformNamePrefix() + "-transactions");
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        p.apply(transformNameSuffix + "-ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(chainConfig.getPubSubSubscriptionPrefix() + "transactions"))
                .apply(transformNameSuffix + "-PartitioningToKV", ParDo.of(new DoFn<String, KV<String, String>>() {
                    /*
                     * NOTE:
                     * In order to work with timers, Beam need require input
                     * to be in (key,value) pairs
                     *
                     * This will work but is against distributed principles.
                     * Beam tries to partition processing across distributed
                     * workers based on 'keys'. However, we assign an empty
                     * string as key to all values. Thus, all records will essentially
                     * be processed on the same worker.
                     *
                     * To avoid this, come up with a 'key'
                     * I cannot do it, since I'm not familiar with the code internals
                     */
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        c.output(KV.of("", c.element()));
                    }
                }))
                .apply(transformNameSuffix + "-ETL", ParDo.of(new DoFn<KV<String, String>, String>() {

                    private static final int MAX_BUFFER_SIZE = 200;
                    @StateId("buffer")
                    private final StateSpec<BagState<KV<String, String>>> bufferedEvents = StateSpecs.bag();
                    @StateId("count")
                    private final StateSpec<ValueState<Integer>> countState = StateSpecs.value();
                    private final Duration MAX_BUFFER_DURATION = Duration.standardSeconds(3);
                    @TimerId("stale")
                    private final TimerSpec staleSpec = TimerSpecs.timer(TimeDomain.PROCESSING_TIME);

                    private void publishItemsToTG(List<Transaction> txns) throws Exception {
                        StringBuilder linkInputs = new StringBuilder();
                        StringBuilder linkOutputs = new StringBuilder();
                        StringBuilder linkFlat = new StringBuilder();
                        StringBuilder transactions = new StringBuilder();
                        for (Transaction transaction : txns) {
                            if (transaction.getCoinbase() == 1) {
                                linkInputs.append(String.format("%s,%s,%s,%s",
                                        transaction.getTransactionId(),
                                        "Newly Generated Coins",
                                        0,
                                        0));
                                linkInputs.append("\n");
                            }

                            for (TransactionInput input : transaction.getInputs()) {
                                linkInputs.append(String.format("%s,%s,%s,%s",
                                        transaction.getTransactionId(),
                                        input.getAddresses(),
                                        transaction.getGroupedInputs().get(
                                                input.getAddresses()
                                        ) / Math.pow(10, 8),
                                        transaction.getGroupedInputs().get(
                                                input.getAddresses()
                                        )  / Math.pow(10, 8) * CryptoCompare.get_hourly_price(currencyCode)));
                                linkInputs.append("\n");
                            }

                            for (int i = 0; i < transaction.getOutputs().size(); i++) {
                                linkOutputs.append(String.format("%s,%s,%s,%s",
                                        transaction.getTransactionId(),
                                        transaction.getOutputs().get(i).getAddresses(),
                                        transaction.getGroupedOutputs().get(
                                                transaction.getOutputs().get(i).getAddresses()
                                        ) / Math.pow(10, 8),
                                        transaction.getGroupedOutputs().get(
                                                transaction.getOutputs().get(i).getAddresses()
                                        ) / Math.pow(10, 8)
                                                * CryptoCompare.get_hourly_price(currencyCode)));
                                linkOutputs.append("\n");

                                for (int j = 0; j < transaction.getInputs().size(); j++) {
                                    linkFlat.append(String.format("%s,%s,%s",
                                            transaction.getInputs().get(j).getAddresses(),
                                            transaction.getOutputs().get(i).getAddresses(),
                                            transaction.getBlockDateTime()));
                                    linkFlat.append("\n");
                                }
                            }
                            transactions.append(String.format("%s,%s,%s,%s,%s,%s",
                                    transaction.getTransactionId(),
                                    transaction.getOutputValue() / Math.pow(10, 8),
                                    transaction.getOutputValue() / Math.pow(10, 8) * transaction.getCoinPriceUSD(),
                                    transaction.getBlockDateTime(),
                                    transaction.getFee() / Math.pow(10, 8),
                                    transaction.getFee() / Math.pow(10, 8) * CryptoCompare.get_hourly_price(currencyCode)));
                            transactions.append("\n");
                        }

                        tigerGraphPost(tigergraphHosts, chain, linkInputs.toString(), "daily_links_inputs");
                        tigerGraphPost(tigergraphHosts, chain, linkOutputs.toString(), "daily_links_outputs");
                        tigerGraphPost(tigergraphHosts, chain, transactions.toString(), "daily_transactions");
                        tigerGraphPost(tigergraphHosts, chain, linkFlat.toString(), "streaming_links_flat");
                    }

                    @OnTimer("stale")
                    public void onStale(
                            OnTimerContext context,
                            @StateId("buffer") BagState<KV<String, String>> bufferState,
                            @StateId("count") ValueState<Integer> countState) throws Exception {

                        System.out.print("stale");
                        List<Transaction> fullBatchResults = Lists.newArrayList();

                        if (!bufferState.isEmpty().read()) {
                            for (KV<String, String> item : bufferState.read()) {
                                fullBatchResults.add(JsonUtils.parseJson(item.getValue(), Transaction.class));
                            }

                            publishItemsToTG(fullBatchResults);
                            bufferState.clear();
                            countState.clear();
                        }
                    }

                    @ProcessElement
                    public void process(
                            ProcessContext context,
                            BoundedWindow window,
                            @StateId("buffer") BagState<KV<String, String>> bufferState,
                            @StateId("count") ValueState<Integer> countState,
                            @TimerId("stale") Timer staleTimer) throws Exception {

                        if (firstNonNull(countState.read(), 0) == 0) {
                            staleTimer.offset(MAX_BUFFER_DURATION).setRelative();
                        }


                        int count = firstNonNull(countState.read(), 0);
                        count = count + 1;
                        countState.write(count);
                        bufferState.add(context.element());

                        List<Transaction> fullBatchResults = Lists.newArrayList();
                        if (count >= MAX_BUFFER_SIZE) {
                            for (KV<String, String> item : bufferState.read()) {
                                fullBatchResults.add(JsonUtils.parseJson(item.getValue(), Transaction.class));
                            }

                            publishItemsToTG(fullBatchResults);
                            bufferState.clear();
                            countState.clear();
                        }
                    }
                }));
    }
}
