package io.blockchainetl.bitcoin;

import io.blockchainetl.bitcoin.clickhouse.Schemas;
import io.blockchainetl.bitcoin.domain.Block;
import io.blockchainetl.bitcoin.domain.Transaction;
import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import io.blockchainetl.common.utils.JsonUtils;
import io.blockchainetl.common.utils.StringUtils;
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


public class TransactionsBlocksClickhousePipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionsBlocksClickhousePipeline.class);

    private static final String PUBSUB_ID_ATTRIBUTE = "item_id";

    public static void runPipeline(
            PubSubToClickhousePipelineOptions options,
            ChainConfig chainConfig
    ) throws Exception {
        Pipeline p = Pipeline.create(options);

        buildTransactionPipeline(p, options, chainConfig);
        buildBlockPipeline(p, options, chainConfig);

        PipelineResult pipelineResult = p.run();
        LOG.info(pipelineResult.toString());
    }

    public static void buildBlockPipeline(Pipeline p, PubSubToClickhousePipelineOptions options, ChainConfig chainConfig) throws Exception {
        String transformNameSuffix = StringUtils.capitalizeFirstLetter(chainConfig.getTransformNamePrefix() + "-blocks");

        p.apply(transformNameSuffix + "-ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(chainConfig.getPubSubSubscriptionPrefix() + "blocks"))
                .apply(transformNameSuffix + "-ETL", ParDo.of(new DoFn<String, Row>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        try {
                            String item = c.element();
                            Block block = JsonUtils.parseJson(item, Block.class);
                            c.output(Row.withSchema(Schemas.BLOCKS)
                                    .addValues(block.getHash(),
                                            block.getNumber(),
                                            block.getDateTime(),
                                            block.getMedianTimestamp(),
                                            block.getMerkleRoot(),
                                            block.getCoinbaseParam(),
                                            block.getCoinbaseParamDecoded(),
                                            block.getCoinbaseTxid(),
                                            block.getNonce(),
                                            block.getDifficulty(),
                                            block.getChainWork(),
                                            block.getVersion(),
                                            block.getVersionHex(),
                                            block.getSize(),
                                            block.getStrippedSize(),
                                            block.getWeight(),
                                            block.getBits(),
                                            block.getBlockReward(),
                                            block.getCoinPriceUSD()).build());
                        } catch (Exception e) {
                            LOG.error("Failed to process input {}.", c.element(), e);
                            // OutOfMemoryError should be retried
                            if (e.getCause() instanceof OutOfMemoryError ||
                                    (e.getCause() != null && e.getCause().getCause() instanceof OutOfMemoryError)) {
                                throw e;
                            }
                        }
                    }

                })).setRowSchema(Schemas.BLOCKS).apply(
                ClickHouseIO.<Row>write(
                        chainConfig.getClickhouseJDBCURI(),
                        chainConfig.getBlocksTable())
                        .withMaxRetries(3)
                        .withMaxInsertBlockSize(1000)
                        .withInitialBackoff(Duration.standardSeconds(5))
                        .withInsertDeduplicate(true)
                        .withInsertDistributedSync(false));
    }

    public static void buildTransactionPipeline(Pipeline p, PubSubToClickhousePipelineOptions options, ChainConfig chainConfig) {
        String transformNameSuffix = StringUtils.capitalizeFirstLetter(chainConfig.getTransformNamePrefix() + "-transactions");

        p.apply(transformNameSuffix + "-ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(chainConfig.getPubSubSubscriptionPrefix() + "transactions"))
                .apply(transformNameSuffix + "-ETL", ParDo.of(new DoFn<String, Row>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        try {
                            String item = c.element();
                            Transaction transaction = JsonUtils.parseJson(item, Transaction.class);
                            c.output(Row.withSchema(Schemas.TRANSACTIONS)
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
                                            transaction.getOutputsAddress()).build());
                        } catch (Exception e) {
                            LOG.error("Failed to process input {}.", c.element(), e);
                            if (e.getCause() instanceof OutOfMemoryError ||
                                    (e.getCause() != null && e.getCause().getCause() instanceof OutOfMemoryError)) {
                                throw e;
                            }
                        }
                    }
                })).setRowSchema(Schemas.TRANSACTIONS).apply(
                ClickHouseIO.<Row>write(
                        chainConfig.getClickhouseJDBCURI(),
                        chainConfig.getTransactionsTable())
                        .withMaxRetries(10)
                        .withMaxInsertBlockSize(50)
                        .withInitialBackoff(Duration.standardSeconds(15))
                        .withInsertDeduplicate(true)
                        .withInsertDistributedSync(false));
    }
}
