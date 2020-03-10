package io.blockchainetl.ethereum;

import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import io.blockchainetl.common.utils.JsonUtils;
import io.blockchainetl.common.utils.StringUtils;
import io.blockchainetl.ethereum.clickhouse.Schemas;
import io.blockchainetl.ethereum.domain.TokenTransfer;
import io.blockchainetl.ethereum.domain.Trace;
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

import java.util.UUID;

public class TransactionsTracesTokensClickhousePipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionsTracesTokensClickhousePipeline.class);

    private static final String PUBSUB_ID_ATTRIBUTE = "item_id";

    public static void runPipeline(
            PubSubToClickhousePipelineOptions options,
            ChainConfig chainConfig
    ) {
        Pipeline p = Pipeline.create(options);

        buildTransactionPipeline(p, options, chainConfig);
        buildTracesPipeline(p, options, chainConfig);
        buildTokenTransfersPipeline(p, options, chainConfig);

        PipelineResult pipelineResult = p.run();
        LOG.info(pipelineResult.toString());
        LOG.info(pipelineResult.toString());
        LOG.info(pipelineResult.toString());
    }

    private static void buildTokenTransfersPipeline(Pipeline tokenTransfers, PubSubToClickhousePipelineOptions options, ChainConfig chainConfig) {
        String transformNameSuffix = StringUtils.capitalizeFirstLetter(chainConfig.getTransformNamePrefix() + "-token_transfers");

        tokenTransfers.apply(transformNameSuffix + "ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(chainConfig.getPubSubSubscriptionPrefix() + "token_transfers").withIdAttribute(PUBSUB_ID_ATTRIBUTE))
                .apply(transformNameSuffix + "ReadFromPubSub", ParDo.of(new DoFn<String, Row>() {
                    @DoFn.ProcessElement
                    public void processElement(ProcessContext c) {
                        String item = c.element();
                        TokenTransfer tokenTransfer = JsonUtils.parseJson(item, TokenTransfer.class);
                        c.output(Row.withSchema(Schemas.MASTER)
                                .addValues(tokenTransfer.getTransactionHash(),
                                        tokenTransfer.getFromAddress(),
                                        tokenTransfer.getToAddress(),
                                        (short) 2,
                                        tokenTransfer.getValue(),
                                        tokenTransfer.getTokenAddress(),
                                        tokenTransfer.getBlockDateTime(),
                                        tokenTransfer.getBlockNumber(),
                                        "0",
                                        0L,
                                        tokenTransfer.getLogIndex(),
                                        (short) 1,
                                        UUID.randomUUID().toString()).build());
                    }

                })).setRowSchema(Schemas.MASTER).apply(
                ClickHouseIO.<Row>write(
                        chainConfig.getClickhouseJDBCURI(),
                        chainConfig.getTransactionsTable())
                        .withMaxRetries(3)
                        .withMaxInsertBlockSize(1000)
                        .withInitialBackoff(Duration.standardSeconds(5))
                        .withInsertDeduplicate(true)
                        .withInsertDistributedSync(false));


    }

    private static void buildTracesPipeline(Pipeline traces, PubSubToClickhousePipelineOptions options, ChainConfig chainConfig) {
        String transformNameSuffix = StringUtils.capitalizeFirstLetter(chainConfig.getTransformNamePrefix() + "-traces");

        traces.apply(transformNameSuffix + "ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(chainConfig.getPubSubSubscriptionPrefix() + "traces").withIdAttribute(PUBSUB_ID_ATTRIBUTE))
                .apply(transformNameSuffix + "ReadFromPubSub", ParDo.of(new DoFn<String, Row>() {
                    @DoFn.ProcessElement
                    public void processElement(ProcessContext c) {
                        String item = c.element();
                        Trace traces = JsonUtils.parseJson(item, Trace.class);
                        System.out.println(traces.getFromAddress());
                        c.output(Row.withSchema(Schemas.MASTER)
                                .addValues(traces.getTransactionHash(),
                                        traces.getFromAddress(),
                                        traces.getToAddress(),
                                        (short) 1,
                                        traces.getValue(),
                                        "0x0000",
                                        traces.getBlockDateTime(),
                                        traces.getBlockNumber(),
                                        "0",
                                        traces.getGas(),
                                        0,
                                        (short) traces.getStatus(),
                                        UUID.randomUUID().toString()).build());
                    }

                })).setRowSchema(Schemas.MASTER).apply(
                ClickHouseIO.<Row>write(
                        chainConfig.getClickhouseJDBCURI(),
                        chainConfig.getTransactionsTable())
                        .withMaxRetries(3)
                        .withMaxInsertBlockSize(1000)
                        .withInitialBackoff(Duration.standardSeconds(5))
                        .withInsertDeduplicate(true)
                        .withInsertDistributedSync(false));
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
                                .addValues(
                                        transaction.getHash(),
                                        transaction.getFromAddress(),
                                        transaction.getToAddress(),
                                        (short) 0,
                                        transaction.getValue(),
                                        "0x0000",
                                        transaction.getBlockDateTime(),
                                        transaction.getBlockNumber(),
                                        String.valueOf(transaction.getReceiptGasUsed() * (transaction.getGasPrice() / Math.pow(10, 18))),
                                        transaction.getGas(),
                                        0,
                                        (short) 1,
                                        UUID.randomUUID().toString()).build());
                    }

                })).setRowSchema(Schemas.MASTER).apply(
                ClickHouseIO.<Row>write(
                        chainConfig.getClickhouseJDBCURI(),
                        chainConfig.getTransactionsTable())
                        .withMaxRetries(10)
                        .withMaxInsertBlockSize(100000)
                        .withInitialBackoff(Duration.standardSeconds(5))
                        .withInsertDeduplicate(true)
                        .withInsertDistributedSync(false));

    }
}
