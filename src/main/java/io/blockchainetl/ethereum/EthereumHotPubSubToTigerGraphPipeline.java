package io.blockchainetl.ethereum;


import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.blockchainetl.common.domain.ChainConfig.readChainConfig;

public class EthereumHotPubSubToTigerGraphPipeline {

    private static final Logger LOG =
            LoggerFactory.getLogger(EthereumHotPubSubToTigerGraphPipeline.class);

    public static void main(String[] args) {
        PubSubToClickhousePipelineOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation().as(PubSubToClickhousePipelineOptions.class);

        runEthereumPipeline(options);
    }

    static void runEthereumPipeline(PubSubToClickhousePipelineOptions options) {
        ChainConfig chainConfig = readChainConfig(options.getChainConfigFile());
        TokensMetadataUtils.readTokensMetadata();
        TransactionsTracesTokensTigerGraphPipeline.runPipeline(
                options,
                chainConfig,
                "ethereum",
                "ETH",
                chainConfig.getTigergraphHosts());
    }

}
