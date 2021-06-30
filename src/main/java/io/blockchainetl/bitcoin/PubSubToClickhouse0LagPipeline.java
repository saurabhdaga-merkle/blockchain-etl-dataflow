package io.blockchainetl.bitcoin;

import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

import static io.blockchainetl.bitcoin.TransactionsBlocks0LagClickhousePipeline.runPipeline;
import static io.blockchainetl.common.domain.ChainConfig.readChainConfig;

public class PubSubToClickhouse0LagPipeline {

    public static void main(String[] args) throws Exception {
        PubSubToClickhousePipelineOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation().as(PubSubToClickhousePipelineOptions.class);

        runBitcoinPipeline(options);
    }

    static void runBitcoinPipeline(PubSubToClickhousePipelineOptions options) throws Exception {
        ChainConfig chainConfigs = readChainConfig(options);
        runPipeline(options, chainConfigs);
    }
}
