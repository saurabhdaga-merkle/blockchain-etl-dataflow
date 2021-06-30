package io.blockchainetl.ripple;

import com.google.api.client.util.Lists;
import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import io.blockchainetl.common.utils.JsonUtils;
import io.blockchainetl.common.utils.StringUtils;
import io.blockchainetl.common.utils.TokenPrices;
import io.blockchainetl.ripple.domain.Payments;
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


public class PaymentsTigerGraphPipeline {

    private static final Logger LOG =
            LoggerFactory.getLogger(PaymentsTigerGraphPipeline.class);

    public static void runPipeline(
            PubSubToClickhousePipelineOptions options,
            ChainConfig chainConfig
    ) {
        Pipeline p = Pipeline.create(options);

        buildTransactionPipeline(
                p,
                chainConfig.getCurrencyCode(),
                chainConfig.getPubSubFullSubscriptionPrefix(),
                chainConfig.getTigergraphHost()
        );

        PipelineResult pipelineResult = p.run();
        LOG.info(pipelineResult.toString());
    }

    public static void buildTransactionPipeline(Pipeline p,
                                                String currencyCode,
                                                String pubSubPrefix,
                                                String tigergraphHost
    ) {
        String transformNameSuffix =
                StringUtils.capitalizeFirstLetter(currencyCode + "-payments");

        p.apply(transformNameSuffix + "-ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(pubSubPrefix + "payments"))
                .apply(transformNameSuffix + "-PartitioningToKV", ParDo.of(new DoFn<String, KV<String, String>>() {

                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        c.output(KV.of(String.valueOf(c.element().hashCode() % 100), c.element()));
                    }
                }))
                .apply(transformNameSuffix + "-ETL", ParDo.of(new DoFn<KV<String, String>, String>() {

                    private static final int MAX_BUFFER_SIZE = 100;
                    @StateId("buffer")
                    private final StateSpec<BagState<KV<String, String>>> bufferedEvents = StateSpecs.bag();
                    @StateId("count")
                    private final StateSpec<ValueState<Integer>> countState = StateSpecs.value();
                    private final Duration MAX_BUFFER_DURATION = Duration.standardSeconds(1);
                    @TimerId("stale")
                    private final TimerSpec staleSpec = TimerSpecs.timer(TimeDomain.PROCESSING_TIME);

                    private void publishItemsToTG(List<Payments> payments) throws Exception {

                        StringBuilder linkFlat = new StringBuilder();
                        StringBuilder chainState = new StringBuilder();

                        for (Payments eachPayment : payments) {
                            linkFlat.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                                    eachPayment.getHash(),
                                    eachPayment.getAccount(),
                                    eachPayment.getDestination(),
                                    (eachPayment.getDeliveredAmount()
                                            + eachPayment.getFee())
                                            / Math.pow(10, 6),
                                    eachPayment.getDeliveredAmount()
                                            / Math.pow(10, 6),
                                    (eachPayment.getDeliveredAmount()
                                            + eachPayment.getFee())
                                            / Math.pow(10, 6)
                                            * TokenPrices.get_hourly_price(currencyCode),
                                    eachPayment.getDeliveredAmount()
                                            / Math.pow(10, 6)
                                            * TokenPrices.get_hourly_price(currencyCode),
                                    eachPayment.getFee() / Math.pow(10, 6),
                                    eachPayment.getFee() / Math.pow(10, 6)
                                            * TokenPrices.get_hourly_price(currencyCode),
                                    eachPayment.getExecutedTime()));
                            linkFlat.append("\n");

                            chainState.append(String.format("%s,%s,%s", "ripple",
                                    eachPayment.getExecutedTime(),
                                    eachPayment.getLedgerIndex())
                            ).append('\n');
                        }

                        tigerGraphPost(tigergraphHost, "ripple", chainState.toString(), "streaming_chainstate");
                        tigerGraphPost(tigergraphHost, "ripple", linkFlat.toString(), "streaming_address_links_flat");
                    }

                    @OnTimer("stale")
                    public void onStale(
                            OnTimerContext context,
                            @StateId("buffer") BagState<KV<String, String>> bufferState,
                            @StateId("count") ValueState<Integer> countState) throws Exception {

                        List<Payments> fullBatchResults = Lists.newArrayList();

                        if (!bufferState.isEmpty().read()) {
                            for (KV<String, String> item : bufferState.read()) {
                                fullBatchResults.add(JsonUtils.parseJson(item.getValue(), Payments.class));
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

                        List<Payments> fullBatchResults = Lists.newArrayList();
                        if (count >= MAX_BUFFER_SIZE) {
                            for (KV<String, String> item : bufferState.read()) {
                                fullBatchResults.add(JsonUtils.parseJson(item.getValue(), Payments.class));
                            }

                            publishItemsToTG(fullBatchResults);
                            bufferState.clear();
                            countState.clear();
                        }
                    }
                }));
    }
}
