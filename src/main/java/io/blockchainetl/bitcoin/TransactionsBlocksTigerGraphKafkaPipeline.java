package io.blockchainetl.bitcoin;

import com.google.common.collect.ImmutableMap;
import io.blockchainetl.bitcoin.domain.Transaction;
import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import io.blockchainetl.common.utils.JsonUtils;
import io.blockchainetl.common.utils.StringUtils;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TransactionsBlocksTigerGraphKafkaPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionsBlocksTigerGraphKafkaPipeline.class);

    private static final String PUBSUB_ID_ATTRIBUTE = "item_id";

    public static void runPipeline(
            PubSubToClickhousePipelineOptions options,
            ChainConfig chainConfig,
            String chain,
            String currencyCode
    ) {
        Pipeline p = Pipeline.create(options);

        buildTransactionPipeline(p, options, chainConfig, chain, currencyCode);

        PipelineResult pipelineResult = p.run();
        LOG.info(pipelineResult.toString());
    }


    public static void buildTransactionPipeline(Pipeline p,
                                                PubSubToClickhousePipelineOptions options,
                                                ChainConfig chainConfig,
                                                String chain,
                                                String currencyCode
                                                ) {
        String transformNameSuffix = StringUtils.capitalizeFirstLetter(chainConfig.getTransformNamePrefix() + "-transactions");

        p.apply(transformNameSuffix + "-ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(chainConfig.getPubSubSubscriptionPrefix() + "transactions"))
                .apply(transformNameSuffix + "-ETL", ParDo.of(new DoFn<String, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        try {
                            String item = c.element();
                            Transaction transaction = JsonUtils.parseJson(item, Transaction.class);
                            c.output(String.format("%s,%s,%s",
                                    chain,
                                    transaction.getBlockDateTime(),
                                    transaction.getBlockNumber()
                            ));
                        } catch (Exception e) {
                            LOG.error("Failed to process input {}.", c.element(), e);
                            if (e.getCause() instanceof OutOfMemoryError ||
                                    (e.getCause() != null && e.getCause().getCause() instanceof OutOfMemoryError)) {
                                throw e;
                            }
                        }
                    }
                })).apply("ToKafka",
                        KafkaIO.<Void, String>write()
                                .withBootstrapServers("z-1.tg-streaming.lj52u2.c4.kafka.us-east-2.amazonaws.com:2181," +
                                        "z-3.tg-streaming.lj52u2.c4.kafka.us-east-2.amazonaws.com:2181," +
                                        "z-2.tg-streaming.lj52u2.c4.kafka.us-east-2.amazonaws.com:2181")
                                .withTopic("litecoin-chainstate")
                                .withValueSerializer(StringSerializer.class)
                                .updateProducerProperties(ImmutableMap.of("security.protocol","PLAINTEXT"))
                                .updateProducerProperties(ImmutableMap.of("sasl.mechanism","PLAIN"))
                                .values())
                ;
    }
}
