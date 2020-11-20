package io.blockchainetl.bitcoin;

import io.blockchainetl.bitcoin.domain.Transaction;
import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import io.blockchainetl.common.utils.JsonUtils;
import io.blockchainetl.common.utils.StringUtils;
import org.apache.beam.repackaged.core.org.apache.commons.lang3.ObjectUtils;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;


public class TransactionsBlocksTigerGraphPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionsBlocksTigerGraphPipeline.class);

    private static final String PUBSUB_ID_ATTRIBUTE = "item_id";

    public static void runPipeline(
            PubSubToClickhousePipelineOptions options,
            ChainConfig chainConfig
    ) throws Exception {
        Pipeline p = Pipeline.create(options);

        buildTransactionPipeline(p, options, chainConfig);

        PipelineResult pipelineResult = p.run();
        LOG.info(pipelineResult.toString());
    }

    public static void buildTransactionPipeline(Pipeline p, PubSubToClickhousePipelineOptions options, ChainConfig chainConfig) {
        String transformNameSuffix = StringUtils.capitalizeFirstLetter(chainConfig.getTransformNamePrefix() + "-transactions");
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");

        PCollection<String> transactions = p.apply(transformNameSuffix + "-ReadFromPubSub",
                PubsubIO.readStrings().fromSubscription(chainConfig.getPubSubSubscriptionPrefix() + "transactions"))
                .apply(transformNameSuffix + "-ETL", ParDo.of(new DoFn<String, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        try {
                            String item = c.element();
                            Transaction transaction = JsonUtils.parseJson(item, Transaction.class);


                            for (int i = 0; i < transaction.getOutputs().size(); i++) {
                                for (int j = 0; j < transaction.getInputs().size(); j++) {

                                    Double outputAmount = transaction.getOutputs().get(i).getValue() / Math.pow(10, 8);
                                    Double inputAmount = transaction.getInputs().get(j).getValue() / Math.pow(10, 8);

                                    Double totolOutputAmount;
                                    if (transaction.getOutputValue() > 0)
                                        totolOutputAmount = Double.valueOf(transaction.getOutputValue()) / Math.pow(10, 8);
                                    else
                                        totolOutputAmount = Double.valueOf(0);

                                    Double totolInputAmount;
                                    if (transaction.getInputValue() > 0)
                                        totolInputAmount = Double.valueOf(transaction.getInputValue()) / Math.pow(10, 8);
                                    else
                                        totolInputAmount = Double.valueOf(0);

                                    Double flattenedOutgoing = inputAmount * outputAmount / totolOutputAmount;
                                    Double flattenedIncoming = inputAmount * outputAmount / totolInputAmount;

                                    if(flattenedOutgoing < 0 || flattenedIncoming < 0) {
                                        LOG.info("#####");
                                        System.out.print(flattenedOutgoing);
                                        System.out.print(flattenedOutgoing * transaction.getCoinPriceUSD());
                                        System.out.print(flattenedIncoming);
                                        System.out.print(flattenedIncoming * transaction.getCoinPriceUSD());
                                        LOG.info("#####");
                                    }
                                    c.output(String.format("%s,%s,%s,%s,%s,%s,%s",
                                            transaction.getInputs().get(j).getAddresses(),
                                            transaction.getOutputs().get(i).getAddresses(),
                                            flattenedOutgoing,
                                            flattenedIncoming,
                                            flattenedOutgoing * transaction.getCoinPriceUSD(),
                                            flattenedIncoming * transaction.getCoinPriceUSD(),
                                            formatter.format(transaction.getBlockDateTime().toDate())));
                                }
                            }


                        } catch (Exception e) {
                            LOG.error("Failed to process input {}.", c.element(), e);
                            if (e.getCause() instanceof OutOfMemoryError ||
                                    (e.getCause() != null && e.getCause().getCause() instanceof OutOfMemoryError)) {
                                throw e;
                            }
                        }
                    }
                }));
        transactions.apply(ParDo.of(new DoFn<String, ObjectUtils.Null>() {
            @ProcessElement
            public void processElement(ProcessContext c) {
                String item = c.element();

                String urly = "http://3.138.208.98:9000/ddl/litecoin?&tag=streaming_links&filename=f1&sep=,&eol=\\n";
                URL obj = null;
                try {
                    obj = new URL(urly);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection con = null;
                try {
                    con = (HttpURLConnection) obj.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    con.setRequestMethod("POST");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                con.setRequestProperty("Content-Type","application/json");


                con.setDoOutput(true);
                DataOutputStream wr = null;
                try {
                    wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(item);
                    wr.flush();
                    wr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                int responseCode = 0;
                try {
                    responseCode = con.getResponseCode();
                    if(responseCode != 200) {
                        LOG.info(item);
                        LOG.info("Response Code : " + responseCode);
                        BufferedReader iny = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));
                        String output;
                        StringBuffer response = new StringBuffer();

                        while ((output = iny.readLine()) != null) {
                            response.append(output);
                        }
                        iny.close();

                        //printing result from response
                        System.out.println(response.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }));
    }
}
