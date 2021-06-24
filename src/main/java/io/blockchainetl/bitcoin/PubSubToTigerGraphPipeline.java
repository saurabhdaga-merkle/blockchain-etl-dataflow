package io.blockchainetl.bitcoin;

import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

import static io.blockchainetl.bitcoin.TransactionsBlocksTigerGraphPipeline.runPipeline;
import static io.blockchainetl.common.domain.ChainConfig.readChainConfig;

public class PubSubToTigerGraphPipeline {

    public static void main(String[] args) throws Exception {
        PubSubToClickhousePipelineOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation().as(PubSubToClickhousePipelineOptions.class);

        runBitcoinPipeline(options);
    }

    static void runBitcoinPipeline(PubSubToClickhousePipelineOptions options) throws IllegalAccessException, InstantiationException {
        runPipeline(options,
                    readChainConfig(options)
        );
    }
}
