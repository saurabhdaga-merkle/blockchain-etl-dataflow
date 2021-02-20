package io.blockchainetl.ripple;

import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import io.blockchainetl.common.utils.JsonUtils;
import io.blockchainetl.common.utils.StringUtils;
import io.blockchainetl.ripple.clickhouse.Schemas;
import io.blockchainetl.ripple.domain.Payments;
import io.blockchainetl.ripple.domain.Reports;
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


public class PaymentsAccountReportsClickhousePipeline {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentsAccountReportsClickhousePipeline.class);

    private static final String PUBSUB_ID_ATTRIBUTE = "item_id";

    public static void runPipeline(
            PubSubToClickhousePipelineOptions options,
            ChainConfig chainConfig
    ) throws Exception {
        Pipeline p = Pipeline.create(options);

        buildPaymentsPipeline(p, options, chainConfig);
        buildAccountReportsPipeline(p, options, chainConfig);

        PipelineResult pipelineResult = p.run();
        LOG.info(pipelineResult.toString());
    }

    private static void buildAccountReportsPipeline(Pipeline p, PubSubToClickhousePipelineOptions options, ChainConfig chainConfig) {
        String transformNameSuffix = StringUtils.capitalizeFirstLetter(chainConfig.getTransformNamePrefix() + "-accounts");

        p.apply(transformNameSuffix + "-ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(chainConfig.getPubSubSubscriptionPrefix() + "account_reports"))
                .apply(transformNameSuffix + "-ETL", ParDo.of(new DoFn<String, Row>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        try {
                            String item = c.element();
                            LOG.info(item);
                            Reports report = JsonUtils.parseJson(item, Reports.class);
                            LOG.info(report.toString());
                            c.output(Row.withSchema(Schemas.Reports)
                                    .addValues(report.getHash(),
                                            report.getExecutedTime(),
                                            report.getLedger_index(),
                                            report.getAccount(),
                                            report.getAmount()
                                    ).build());
                        } catch (Exception e) {
                            LOG.error("Failed to process input {}.", c.element(), e);
                            LOG.info(e.getMessage());

                            if (e.getCause() instanceof OutOfMemoryError ||
                                    (e.getCause() != null && e.getCause().getCause() instanceof OutOfMemoryError)) {
                                throw e;
                            }
                        }
                    }
                })).setRowSchema(Schemas.Reports).apply(
                ClickHouseIO.<Row>write(
                        chainConfig.getClickhouseJDBCURI(),
                        "ripple_address_reports_daily")
                        .withMaxRetries(2)
                        .withMaxInsertBlockSize(100000)
                        .withInitialBackoff(Duration.standardSeconds(5))
                        .withInsertDeduplicate(false)
                        .withInsertDistributedSync(false));
    }


    public static void buildPaymentsPipeline(Pipeline p, PubSubToClickhousePipelineOptions options, ChainConfig chainConfig) {
        String transformNameSuffix = StringUtils.capitalizeFirstLetter(chainConfig.getTransformNamePrefix() + "-payments");

        p.apply(transformNameSuffix + "-ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(chainConfig.getPubSubSubscriptionPrefix() + "payments"))
                .apply(transformNameSuffix + "-ETL", ParDo.of(new DoFn<String, Row>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        try {
                            String item = c.element();
                            Payments payments = JsonUtils.parseJson(item, Payments.class);
                            c.output(Row.withSchema(Schemas.Payments)
                                    .addValues(
                                            payments.getHash(),
                                            payments.getExecutedTime(),
                                            payments.getAccount(),
                                            payments.getDestination(),
                                            payments.getLedgerIndex(),
                                            payments.getDeliveredAmount(),
                                            payments.getFee(),
                                            payments.getSourceTag(),
                                            payments.getDestinationTag()
                                    ).build());
                        } catch (Exception e) {
                            LOG.error("Failed to process input {}.", c.element(), e);
                            if (e.getCause() instanceof OutOfMemoryError ||
                                    (e.getCause() != null && e.getCause().getCause() instanceof OutOfMemoryError)) {
                                throw e;
                            }
                        }
                    }
                })).setRowSchema(Schemas.Payments).apply(
                ClickHouseIO.<Row>write(
                        chainConfig.getClickhouseJDBCURI(),
                        chainConfig.getTransactionsTable())
                        .withMaxRetries(2)
                        .withMaxInsertBlockSize(100000)
                        .withInitialBackoff(Duration.standardSeconds(5))
                        .withInsertDeduplicate(false)
                        .withInsertDistributedSync(false));
    }
}
