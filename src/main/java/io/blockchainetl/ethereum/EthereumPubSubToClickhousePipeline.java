package io.blockchainetl.ethereum;


import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

import static io.blockchainetl.ethereum.TransactionsTracesTokensClickhousePipeline.runPipeline;
import static io.blockchainetl.common.domain.ChainConfig.readChainConfig;

public class EthereumPubSubToClickhousePipeline {

    public static void main(String[] args)
            throws IllegalAccessException, InstantiationException {

        PubSubToClickhousePipelineOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation().as(PubSubToClickhousePipelineOptions.class);

        runEthereumPipeline(options);
    }

    static void runEthereumPipeline(PubSubToClickhousePipelineOptions options)
            throws IllegalAccessException, InstantiationException {

        runPipeline(options,
                    readChainConfig(options)
        );
    }
}
