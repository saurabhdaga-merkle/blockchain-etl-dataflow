package io.blockchainetl.bitcoin;

import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.domain.ChainConfig;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

import static io.blockchainetl.bitcoin.TransactionsBlocksTigerGraphPipeline.runPipeline;
import static io.blockchainetl.common.domain.ChainConfig.readChainConfig;

public class BitcoinCashPubSubToTigerGraphPipeline {

    public static void main(String[] args) throws Exception {
        PubSubToClickhousePipelineOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation().as(PubSubToClickhousePipelineOptions.class);

        runBitcoinPipeline(options);
    }

    static void runBitcoinPipeline(PubSubToClickhousePipelineOptions options) throws Exception {
        ChainConfig chainConfigs = readChainConfig(options.getChainConfigFile());
        TransactionsBlocksTigerGraphPipeline.runPipeline(options,
                chainConfigs,
                "bitcoin_cash",
                "BCH",
                chainConfigs.getTigergraphHosts());
    }
}