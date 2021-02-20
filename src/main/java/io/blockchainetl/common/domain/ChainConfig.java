package io.blockchainetl.common.domain;

import io.blockchainetl.common.utils.FileUtils;
import io.blockchainetl.common.utils.JsonUtils;
import org.codehaus.jackson.type.TypeReference;

import java.nio.charset.Charset;

public class ChainConfig {

    private String transformNamePrefix;
    private String pubSubSubscriptionPrefix;
    private String clickhouseDatabase;
    private String transactionsTable;
    private String blocksTable;
    private String clickhouseJDBCURI;
    private String startTimestamp;
    private String tigergraphHosts;
    private boolean isHotFlow;

    public static ChainConfig readChainConfig(String file) {
        String fileContents = FileUtils.readFile(file, Charset.forName("UTF-8"));
        ChainConfig result = JsonUtils.parseJson(fileContents, new TypeReference<ChainConfig>() {
        });
        return result;
    }

    public String getTransformNamePrefix() {
        return transformNamePrefix;
    }

    public String getPubSubSubscriptionPrefix() {
        return pubSubSubscriptionPrefix;
    }

    public boolean isHotFlow() {
        return isHotFlow;
    }

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public String getTransactionsTable() {
        return transactionsTable;
    }

    public String getClickhouseJDBCURI() {
        return clickhouseJDBCURI;
    }

    public String getBlocksTable() {
        return blocksTable;
    }

    public String[] getTigergraphHosts() {
        return tigergraphHosts.split(";");
    }
}
