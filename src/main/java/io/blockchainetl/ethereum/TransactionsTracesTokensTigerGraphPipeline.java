package io.blockchainetl.ethereum;

import com.google.api.client.util.Lists;
import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import io.blockchainetl.common.utils.JsonUtils;
import io.blockchainetl.common.utils.StringUtils;
import io.blockchainetl.common.utils.TokenPrices;
import io.blockchainetl.ethereum.domain.Token;
import io.blockchainetl.ethereum.domain.TokenTransfer;
import io.blockchainetl.ethereum.domain.Trace;
import io.blockchainetl.ethereum.domain.Transaction;
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
import java.util.Map;

import static io.blockchainetl.common.tigergraph.Utils.tigerGraphPost;
import static org.apache.beam.vendor.guava.v26_0_jre.com.google.common.base.MoreObjects.firstNonNull;


public class TransactionsTracesTokensTigerGraphPipeline {

    private static final Logger LOG =
            LoggerFactory.getLogger(TransactionsTracesTokensTigerGraphPipeline.class);

    private static final int MAX_BUFFER_SIZE = 50;
    private static final Duration MAX_BUFFER_DURATION = Duration.standardSeconds(1);

    public static void runPipeline(
            PubSubToClickhousePipelineOptions options,
            ChainConfig chainConfig
    ) {
        Pipeline p = Pipeline.create(options);


        buildTransactionPipeline(
                p,
                chainConfig.getPubSubFullSubscriptionPrefix(),
                chainConfig.getCurrency(),
                chainConfig.getCurrencyCode(),
                chainConfig.getTigergraphHost()
        );

        buildTracesPipeline(
                p,
                chainConfig.getPubSubFullSubscriptionPrefix(),
                chainConfig.getCurrency(),
                chainConfig.getCurrencyCode(),
                chainConfig.getTigergraphHost());

        buildTokenTransfersPipeline(
                p,
                chainConfig.getPubSubFullSubscriptionPrefix(),
                chainConfig.getCurrency(),
                chainConfig.getTigergraphHost(),
                chainConfig.getTokensMetadata());

        PipelineResult pipelineResult = p.run();
        LOG.info(pipelineResult.toString());
    }

    private static void buildTokenTransfersPipeline(Pipeline p,
                                                    String pubsubSubscription,
                                                    String currency,
                                                    String tigergraphHost,
                                                    Map<String, Token> tokensMetadata
    ) {
        String transformNameSuffix =
                StringUtils.capitalizeFirstLetter(currency + "-token-transfers");
        String subscription = pubsubSubscription + "token_transfers";

        p.apply(transformNameSuffix + "-ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(subscription))
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
                        c.output(KV.of(String.valueOf(c.element().hashCode() % 50), c.element()));
                    }
                }))
                .apply(transformNameSuffix + "-ETL", ParDo.of(new DoFn<KV<String, String>, String>() {

                    @StateId("buffer")
                    private final StateSpec<BagState<KV<String, String>>> bufferedEvents = StateSpecs.bag();
                    @StateId("count")
                    private final StateSpec<ValueState<Integer>> countState = StateSpecs.value();
                    @TimerId("stale")
                    private final TimerSpec staleSpec = TimerSpecs.timer(TimeDomain.PROCESSING_TIME);

                    private void publishItemsToTG(List<TokenTransfer> tokenTransfers) throws Exception {

                        StringBuilder linkFlat = new StringBuilder();

                        for (TokenTransfer eachTokenTransfer : tokenTransfers) {
                            if (tokensMetadata.containsKey(eachTokenTransfer.getTokenAddress())) {
                                LOG.info(String.valueOf(TokenPrices.coinPrices));
                                LOG.info(TokenPrices.get_hourly_price(tokensMetadata.get(eachTokenTransfer.getTokenAddress()).getSymbol()) +
                                                 "tx=" + eachTokenTransfer.getTransactionHash());
                                linkFlat.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                                                              eachTokenTransfer.getTransactionHash(),
                                                              eachTokenTransfer.getFromAddress(),
                                                              eachTokenTransfer.getToAddress(),
                                                              tokensMetadata.get(eachTokenTransfer.getTokenAddress()).getSymbol(),
                                                              2,
                                                              Double.parseDouble(eachTokenTransfer.getValue())
                                                                      / Math.pow(10, tokensMetadata.get(eachTokenTransfer.getTokenAddress()).getDecimals()),
                                                              Double.parseDouble(eachTokenTransfer.getValue())
                                                                      / Math.pow(10, tokensMetadata.get(eachTokenTransfer.getTokenAddress()).getDecimals())
                                                                      * TokenPrices.get_hourly_price(tokensMetadata.get(
                                                                      eachTokenTransfer.getTokenAddress()).getSymbol()),
                                                              eachTokenTransfer.getBlockDateTime(),
                                                              0,
                                                              0
                                )).append("\n");
                            }
                        }

                        tigerGraphPost(tigergraphHost, currency, linkFlat.toString(), "streaming_address_links_flat");
                    }

                    @OnTimer("stale")
                    public void onStale(
                            OnTimerContext context,
                            @StateId("buffer") BagState<KV<String, String>> bufferState,
                            @StateId("count") ValueState<Integer> countState) throws Exception {

                        System.out.print("stale");
                        List<TokenTransfer> fullBatchResults = Lists.newArrayList();

                        if (!bufferState.isEmpty().read()) {
                            for (KV<String, String> item : bufferState.read()) {
                                fullBatchResults.add(JsonUtils.parseJson(item.getValue(), TokenTransfer.class));
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

                        List<TokenTransfer> fullBatchResults = Lists.newArrayList();
                        if (count >= MAX_BUFFER_SIZE) {
                            for (KV<String, String> item : bufferState.read()) {
                                fullBatchResults.add(JsonUtils.parseJson(item.getValue(), TokenTransfer.class));
                            }

                            publishItemsToTG(fullBatchResults);
                            bufferState.clear();
                            countState.clear();
                        }
                    }
                }));
    }

    private static void buildTracesPipeline(Pipeline p,
                                            String pubsubSubscription,
                                            String currency,
                                            String currencyCode,
                                            String tigergraphHost
    ) {

        String transformNameSuffix =
                StringUtils.capitalizeFirstLetter(currency + "-traces");
        String subscription = pubsubSubscription + "traces";

        p.apply(transformNameSuffix + "-ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(subscription))
                .apply(transformNameSuffix + "-PartitioningToKV", ParDo.of(new DoFn<String, KV<String, String>>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        c.output(KV.of(String.valueOf(c.element().hashCode() % 50), c.element()));
                    }
                }))
                .apply(transformNameSuffix + "-ETL", ParDo.of(new DoFn<KV<String, String>, String>() {

                    @StateId("buffer")
                    private final StateSpec<BagState<KV<String, String>>> bufferedEvents = StateSpecs.bag();
                    @StateId("count")
                    private final StateSpec<ValueState<Integer>> countState = StateSpecs.value();
                    @TimerId("stale")
                    private final TimerSpec staleSpec = TimerSpecs.timer(TimeDomain.PROCESSING_TIME);

                    private void publishItemsToTG(List<Trace> traces) throws Exception {

                        StringBuilder linkFlat = new StringBuilder();

                        for (Trace eachTrace : traces) {
                            linkFlat.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                                                          eachTrace.getTransactionHash(),
                                                          eachTrace.getFromAddress(),
                                                          eachTrace.getToAddress(),
                                                          currencyCode,
                                                          1,
                                                          Double.parseDouble(eachTrace.getValue()) / Math.pow(10, 18),
                                                          Double.parseDouble(eachTrace.getValue()) / Math.pow(10, 18)
                                                                  * TokenPrices.get_hourly_price(currencyCode),
                                                          eachTrace.getBlockDateTime(),
                                                          0,
                                                          0
                            )).append("\n");
                        }

                        tigerGraphPost(tigergraphHost, currency, linkFlat.toString(), "streaming_address_links_flat");
                    }

                    @OnTimer("stale")
                    public void onStale(
                            OnTimerContext context,
                            @StateId("buffer") BagState<KV<String, String>> bufferState,
                            @StateId("count") ValueState<Integer> countState) throws Exception {

                        LOG.info("stale");
                        List<Trace> fullBatchResults = Lists.newArrayList();

                        if (!bufferState.isEmpty().read()) {
                            for (KV<String, String> item : bufferState.read()) {
                                fullBatchResults.add(JsonUtils.parseJson(item.getValue(), Trace.class));
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

                        List<Trace> fullBatchResults = Lists.newArrayList();
                        if (count >= MAX_BUFFER_SIZE) {
                            for (KV<String, String> item : bufferState.read()) {
                                fullBatchResults.add(JsonUtils.parseJson(item.getValue(), Trace.class));
                            }

                            publishItemsToTG(fullBatchResults);
                            bufferState.clear();
                            countState.clear();
                        }
                    }
                }));
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
                        c.output(KV.of(String.valueOf(c.element().hashCode() % 50), c.element()));
                    }
                }))
                .apply(transformNameSuffix + "-ETL", ParDo.of(new DoFn<KV<String, String>, String>() {

                    @StateId("buffer")
                    private final StateSpec<BagState<KV<String, String>>> bufferedEvents = StateSpecs.bag();
                    @StateId("count")
                    private final StateSpec<ValueState<Integer>> countState = StateSpecs.value();
                    @TimerId("stale")
                    private final TimerSpec staleSpec = TimerSpecs.timer(TimeDomain.PROCESSING_TIME);

                    private void publishItemsToTG(List<Transaction> txns) throws Exception {

                        StringBuilder linkFlat = new StringBuilder();
                        StringBuilder chainState = new StringBuilder();

                        for (Transaction eachTransaction : txns) {
                            linkFlat.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                                                          eachTransaction.getHash(),
                                                          eachTransaction.getFromAddress(),
                                                          eachTransaction.getToAddress(),
                                                          currencyCode,
                                                          0,
                                                          Double.parseDouble(eachTransaction.getValue()) / Math.pow(10, 18),
                                                          Double.parseDouble(eachTransaction.getValue()) / Math.pow(10, 18)
                                                                  * TokenPrices.get_hourly_price(currencyCode),
                                                          eachTransaction.getBlockDateTime(),
                                                          eachTransaction.getReceiptGasUsed() * (eachTransaction.getGasPrice() / Math.pow(10, 18)),
                                                          eachTransaction.getReceiptGasUsed() * (eachTransaction.getGasPrice() / Math.pow(10, 18))
                                                                  * TokenPrices.get_hourly_price(currencyCode)
                            )).append("\n");

                            chainState.append(String.format("%s,%s,%s", currency,
                                                            eachTransaction.getBlockDateTime(),
                                                            eachTransaction.getBlockNumber())
                            ).append('\n');
                        }

                        tigerGraphPost(tigergraphHost, currency, chainState.toString(), "streaming_chainstate");
                        tigerGraphPost(tigergraphHost, currency, linkFlat.toString(), "streaming_address_links_flat");
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
