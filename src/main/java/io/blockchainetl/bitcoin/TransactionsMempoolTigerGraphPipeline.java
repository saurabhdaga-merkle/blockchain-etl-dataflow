package io.blockchainetl.bitcoin;

import com.google.api.client.util.Lists;
import io.blockchainetl.bitcoin.domain.Transaction;
import io.blockchainetl.bitcoin.domain.TransactionInput;
import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import io.blockchainetl.common.utils.JsonUtils;
import io.blockchainetl.common.utils.StringUtils;
import io.blockchainetl.common.utils.TokenPrices;
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

import java.util.List;

import static io.blockchainetl.common.tigergraph.Utils.tigerGraphPost;
import static org.apache.beam.vendor.guava.v26_0_jre.com.google.common.base.MoreObjects.firstNonNull;


public class TransactionsMempoolTigerGraphPipeline {

    private static final Logger LOG =
            LoggerFactory.getLogger(TransactionsMempoolTigerGraphPipeline.class);
    private static DoFn<KV<String, String>, String> microDoFn;

    public static void runPipeline(
            PubSubToClickhousePipelineOptions options,
            ChainConfig chainConfig
    ) {
        Pipeline p = Pipeline.create(options);

        buildTransactionPipeline(p,
                                 chainConfig.getPubSubFullSubscriptionPrefix(),
                                 chainConfig.getCurrency(),
                                 chainConfig.getCurrencyCode(),
                                 chainConfig.getTigergraphHost());

        PipelineResult pipelineResult = p.run();
        LOG.info(pipelineResult.toString());
    }

    public static void buildTransactionPipeline(Pipeline p,
                                                String pubsubSubscription,
                                                String currency,
                                                String currencyCode,
                                                String tigergraphHost) {
        String transformNameSuffix =
                StringUtils.capitalizeFirstLetter(currency + "-transactions");
        String subscription = pubsubSubscription + "transactions";

        p.apply(transformNameSuffix + "-ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(subscription))
                .apply(transformNameSuffix + "-PartitioningToKV", ParDo.of(new DoFn<String, KV<String, String>>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        c.output(KV.of(String.valueOf(c.element().hashCode() % 20), c.element()));
                    }
                }))
                .apply(transformNameSuffix + "-ETL", ParDo.of(new DoFn<KV<String, String>, String>() {

                    private static final int MAX_BUFFER_SIZE = 100;
                    @StateId("buffer")
                    private final StateSpec<BagState<KV<String, String>>> bufferedEvents = StateSpecs.bag();
                    @StateId("count")
                    private final StateSpec<ValueState<Integer>> countState = StateSpecs.value();
                    private final Duration MAX_BUFFER_DURATION = Duration.standardSeconds((long) 0.5);
                    @TimerId("stale")
                    private final TimerSpec staleSpec = TimerSpecs.timer(TimeDomain.PROCESSING_TIME);

                    private void publishItemsToTG(List<Transaction> txns) throws Exception {
                        StringBuilder linkInputs = new StringBuilder();
                        StringBuilder linkOutputs = new StringBuilder();
                        StringBuilder transactions = new StringBuilder();
                        StringBuilder chainState = new StringBuilder();
                        for (Transaction transaction : txns) {
                            if (transaction.getCoinbase() == 1) {
                                linkInputs.append(
                                        String.format(
                                                "%s,%s,%s,%s",
                                                transaction.getTransactionId(),
                                                "Newly Generated Coins",
                                                0,
                                                0));
                                linkInputs.append("\n");
                            }

                            for (TransactionInput input : transaction.getInputs()) {
                                linkInputs.append(
                                        String.format(
                                                "%s,%s,%s,%s",
                                                transaction.getTransactionId(),
                                                input.getAddresses(),
                                                transaction.getGroupedInputs().get(
                                                        input.getAddresses()
                                                ) / Math.pow(10, 8),
                                                transaction.getGroupedInputs().get(
                                                        input.getAddresses()
                                                ) / Math.pow(10, 8) * TokenPrices.get_hourly_price(currencyCode)));
                                linkInputs.append("\n");
                            }

                            for (int i = 0; i < transaction.getOutputs().size(); i++) {
                                linkOutputs.append(
                                        String.format(
                                                "%s,%s,%s,%s",
                                                transaction.getTransactionId(),
                                                transaction.getOutputs().get(i).getAddresses(),
                                                transaction.getGroupedOutputs().get(
                                                        transaction.getOutputs().get(i).getAddresses()
                                                ) / Math.pow(10, 8),
                                                transaction.getGroupedOutputs().get(
                                                        transaction.getOutputs().get(i).getAddresses()
                                                ) / Math.pow(10, 8)
                                                        * TokenPrices.get_hourly_price(currencyCode)));
                                linkOutputs.append("\n");

                            }
                            transactions.append(
                                    String.format(
                                            "%s,%s,%s,%s,%s,%s",
                                            transaction.getTransactionId(),
                                            transaction.getOutputValue() / Math.pow(10, 8),
                                            transaction.getOutputValue() / Math.pow(10, 8) * TokenPrices.get_hourly_price(currencyCode),
                                            transaction.getBlockDateTime(),
                                            transaction.getFee() / Math.pow(10, 8),
                                            transaction.getFee() / Math.pow(10, 8) * TokenPrices.get_hourly_price(currencyCode)));
                            transactions.append("\n");

                            chainState.append(
                                    String.format(
                                            "%s,%s,%s",
                                            currency,
                                            transaction.getBlockDateTime(),
                                            transaction.getBlockNumber())
                            ).append("\n");
                        }

                        tigerGraphPost(tigergraphHost, currency, transactions.toString(), "load_mempool_transactions");
                        tigerGraphPost(tigergraphHost, currency, linkInputs.toString(), "daily_links_inputs");
                        tigerGraphPost(tigergraphHost, currency, linkOutputs.toString(), "daily_links_outputs");
                    }

                    @OnTimer("stale")
                    public void onStale(
                            OnTimerContext context,
                            @StateId("buffer") BagState<KV<String, String>> bufferState,
                            @StateId("count") ValueState<Integer> countState) throws Exception {

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
