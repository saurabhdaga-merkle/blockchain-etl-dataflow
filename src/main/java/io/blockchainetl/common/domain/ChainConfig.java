package io.blockchainetl.common.domain;

import io.blockchainetl.common.utils.FileUtils;
import io.blockchainetl.common.utils.JsonUtils;
import io.blockchainetl.ethereum.TokensMetadataUtils;
import io.blockchainetl.ethereum.domain.Token;
import org.codehaus.jackson.type.TypeReference;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChainConfig {

    private String transformNamePrefix;
    private String pubSubSubscriptionPrefix;
    private String clickhouseDatabase;
    private String transactionsTable;
    private String blocksTable;
    private List<String> clickhouseJDBCURIs;
    private String startTimestamp;
    private String tigergraphHosts;
    private String tokensMetadata;
    private boolean isHotFlow;

    public static ChainConfig readChainConfig(String file) {
        String fileContents = FileUtils.readFile(file, StandardCharsets.UTF_8);
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


    public List<String> getClickhouseJDBCURIs() { return clickhouseJDBCURIs; }

    public String getRandomClickhouseJDBCURI() {
        return clickhouseJDBCURIs.get(new Random().nextInt(clickhouseJDBCURIs.size()));
    }

    public String getBlocksTable() {
        return blocksTable;
    }

    public String[] getTigergraphHosts() {
        return tigergraphHosts.split(";");
    }

    public Map<String, Token> getTokensMetadata() {
        return TokensMetadataUtils.parseTokensMetadata(tokensMetadata);
    }
}
