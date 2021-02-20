package io.blockchainetl.ripple;

import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

import static io.blockchainetl.common.domain.ChainConfig.readChainConfig;
import static io.blockchainetl.ripple.PaymentsTigerGraphPipeline.runPipeline;

public class RipplePubSubToTigerGraphPipeline {

    public static void main(String[] args) throws Exception {
        PubSubToClickhousePipelineOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation().as(PubSubToClickhousePipelineOptions.class);

        runRipplePipeline(options);
    }

    static void runRipplePipeline(PubSubToClickhousePipelineOptions options) throws Exception {
        ChainConfig chainConfigs = readChainConfig(options.getChainConfigFile());
        runPipeline(options,
                chainConfigs,
                "ripple",
                "XRP",
                chainConfigs.getTigergraphHosts()
                );
    }
}
