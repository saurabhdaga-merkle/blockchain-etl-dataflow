package io.blockchainetl.ethereum;

import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import io.blockchainetl.common.utils.JsonUtils;
import io.blockchainetl.common.utils.StringUtils;
import io.blockchainetl.ethereum.clickhouse.Schemas;
import io.blockchainetl.ethereum.domain.Transaction;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.clickhouse.ClickHouseIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.Row;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionsTracesTokensClickhousePipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionsTracesTokensClickhousePipeline.class);

    private static final String PUBSUB_ID_ATTRIBUTE = "item_id";

    public static void runPipeline(
            PubSubToClickhousePipelineOptions options,
            ChainConfig chainConfig
    ) {
        Pipeline transaction = Pipeline.create(options);
        Pipeline traces = Pipeline.create(options);
        Pipeline tokenTransfers = Pipeline.create(options);

        buildTransactionPipeline(transaction, options, chainConfig);
        buildTracesPipeline(traces, options, chainConfig);
        buildTokenTransfersPipeline(tokenTransfers, options, chainConfig);

        PipelineResult pipelineTransactionResult = transaction.run();
        PipelineResult pipelineTracesResult = traces.run();
        PipelineResult pipelineTokenTransfersResult = tokenTransfers.run();
        LOG.info(pipelineTransactionResult.toString());
        LOG.info(pipelineTracesResult.toString());
        LOG.info(pipelineTokenTransfersResult.toString());
    }

    private static void buildTokenTransfersPipeline(Pipeline tokenTransfers, PubSubToClickhousePipelineOptions options, ChainConfig chainConfig) {

    }

    private static void buildTracesPipeline(Pipeline traces, PubSubToClickhousePipelineOptions options, ChainConfig chainConfig) {

    }

    public static void buildTransactionPipeline(Pipeline p, PubSubToClickhousePipelineOptions options, ChainConfig chainConfig) {
        String transformNameSuffix = StringUtils.capitalizeFirstLetter(chainConfig.getTransformNamePrefix() + "-transactions");

        p.apply(transformNameSuffix + "ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(chainConfig.getPubSubSubscriptionPrefix() + "transactions").withIdAttribute(PUBSUB_ID_ATTRIBUTE))
                .apply(transformNameSuffix + "ReadFromPubSub", ParDo.of(new DoFn<String, Row>() {
                    @DoFn.ProcessElement
                    public void processElement(ProcessContext c) {
                        String item = c.element();
                        Transaction transaction = JsonUtils.parseJson(item, Transaction.class);
                        c.output(Row.withSchema(Schemas.MASTER)
                                .addValues(transaction.getHash(),
                                        transaction.getFromAddress(),
                                        transaction.getToAddress(),
                                        transaction.getType(),
                                        transaction.getValue(),
                                        "0x0000",
                                        transaction.getBlockTimestamp(),
                                        transaction.getGas(),
                                        -1L).build());
                    }

                })).setRowSchema(Schemas.MASTER).apply(
                ClickHouseIO.<Row>write(
                        chainConfig.getClickhouseJDBCURI(),
                        chainConfig.getTransactionsTable())
                        .withMaxRetries(3)
                        .withMaxInsertBlockSize(10)
                        .withInitialBackoff(Duration.standardSeconds(5))
                        .withInsertDeduplicate(true)
                        .withInsertDistributedSync(false));

    }
}
