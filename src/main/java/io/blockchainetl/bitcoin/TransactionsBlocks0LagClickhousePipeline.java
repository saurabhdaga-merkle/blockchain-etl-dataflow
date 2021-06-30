package io.blockchainetl.bitcoin;

import io.blockchainetl.bitcoin.clickhouse.Schemas;
import io.blockchainetl.bitcoin.domain.Transaction;
import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import io.blockchainetl.common.domain.Constants;
import io.blockchainetl.common.utils.JsonUtils;
import io.blockchainetl.common.utils.StringUtils;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.clickhouse.ClickHouseIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TransactionsBlocks0LagClickhousePipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionsBlocks0LagClickhousePipeline.class);

    private static final String PUBSUB_ID_ATTRIBUTE = "item_id";

    public static void runPipeline(
            PubSubToClickhousePipelineOptions options,
            ChainConfig chainConfig
    ) throws Exception {
        Pipeline p = Pipeline.create(options);

        buildTransactionPipeline(p, chainConfig);

        PipelineResult pipelineResult = p.run();
        LOG.info(pipelineResult.toString());
    }

    public static void buildTransactionPipeline(Pipeline p, ChainConfig chainConfig) {
        String transformNameSuffix = StringUtils.capitalizeFirstLetter(chainConfig.getTransformNamePrefix() + "-transactions");

        p.apply(transformNameSuffix + "-ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(chainConfig.getPubSubFullSubscriptionPrefix() + "transactions"))
                .apply(transformNameSuffix + "-ETL", ParDo.of(new DoFn<String, Row>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        try {
                            String item = c.element();
                            Transaction transaction = JsonUtils.parseJson(item, Transaction.class);
                            c.output(Row.withSchema(Schemas.TRANSACTIONS_HOT)
                                    .addValues(
                                            transaction.getTransactionId(),
                                            transaction.getHash(),
                                            (long) transaction.getBlockNumber(),
                                            transaction.getBlockHash(),
                                            transaction.getBlockDateTime(),
                                            (short) transaction.getCoinbase(),
                                            transaction.getLockTime(),
                                            transaction.getSize(),
                                            transaction.getVirtualSize(),
                                            transaction.getWeight(),
                                            transaction.getVersion(),
                                            transaction.getInputCount(),
                                            transaction.getOutputCount(),
                                            transaction.getInputValue(),
                                            transaction.getOutputValue(),
                                            transaction.getFee(),
                                            transaction.getCoinPriceUSD(),
                                            transaction.getInputsValue(),
                                            transaction.getInputsType(),
                                            transaction.getInputsRequiredSignatures(),
                                            transaction.getInputsIndex(),
                                            transaction.getInputsCreateTransactionId(),
                                            transaction.getInputsSpendingTransactionId(),
                                            transaction.getInputsCreateOutputIndex(),
                                            transaction.getInputsScriptAsm(),
                                            transaction.getInputsScriptHex(),
                                            transaction.getInputsAddress(),
                                            transaction.getOutputsValue(),
                                            transaction.getOutputsType(),
                                            transaction.getOutputsRequiredSignatures(),
                                            transaction.getOutputsCreateTransactionId(),
                                            transaction.getOutputsIndex(),
                                            transaction.getOutputsScriptAsm(),
                                            transaction.getOutputsScriptHex(),
                                            transaction.getOutputsAddress(),
                                            (byte) 1).build());
                        } catch (Exception e) {
                            LOG.error("Failed to process input {}.", c.element(), e);
                            if (e.getCause() instanceof OutOfMemoryError ||
                                    (e.getCause() != null && e.getCause().getCause() instanceof OutOfMemoryError)) {
                                throw e;
                            }
                        }
                    }
                })).setRowSchema(Schemas.TRANSACTIONS_HOT).apply(
                ClickHouseIO.<Row>write(
                        chainConfig.getRandomClickhouseJDBCURI(),
                        chainConfig.getTransactionsTableHot())
                        .withMaxRetries(Constants.CH_MAX_RETRIES)
                        .withMaxInsertBlockSize(Constants.CH_MAX_INSERT_BLOCK_SIZE)
                        .withInitialBackoff(Constants.CH_INITIAL_BACKOFF_SEC)
                        .withInsertDeduplicate(true)
                        .withInsertDistributedSync(false)
        );
    }
}
