package io.blockchainetl.ethereum;


import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

import static io.blockchainetl.common.domain.ChainConfig.readChainConfig;

public class EthereumHotPubSubToClickhousePipeline {

    public static void main(String[] args) {
        PubSubToClickhousePipelineOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation().as(PubSubToClickhousePipelineOptions.class);

        runEthereumPipeline(options);
    }

    static void runEthereumPipeline(PubSubToClickhousePipelineOptions options) {
        ChainConfig chainConfig = readChainConfig(options.getChainConfigFile());
        TransactionsTracesTokensHotClickhousePipeline.runPipeline(options, chainConfig);
    }
}
