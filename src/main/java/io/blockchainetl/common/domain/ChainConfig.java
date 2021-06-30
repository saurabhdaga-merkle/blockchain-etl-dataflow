package io.blockchainetl.common.domain;

import io.blockchainetl.common.PubSubToClickhousePipelineOptions;
import io.blockchainetl.common.utils.FileUtils;
import io.blockchainetl.common.utils.JsonUtils;
import io.blockchainetl.ethereum.TokensMetadataUtils;
import io.blockchainetl.ethereum.domain.Token;
import org.codehaus.jackson.type.TypeReference;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.blockchainetl.common.utils.JsonUtils.mergeObjects;

/**
 *
 */
public class ChainConfig {

    private static String CONFIGS_PATH = "config/";
    private static String CONFIGS_FILE = "common.json";

    private String pubSubSubscriptionProject;
    private String pubSubFullSubscriptionPrefix;
    private String currency;
    private String transactionsTableHot;
    private String transactionsTable;
    private List<String> clickhouseJDBCURIs;
    private String tigergraphHost;
    private String tokensMetadata;
    private String currencyCode;

    public static ChainConfig readChainConfig(PubSubToClickhousePipelineOptions options) throws IllegalAccessException, InstantiationException {

        String commonConfigs = FileUtils.readFile(CONFIGS_PATH +
                                                          CONFIGS_FILE,
                                                  StandardCharsets.UTF_8);

        String currencyConfigs = FileUtils.readFile(CONFIGS_PATH +
                                                            options.getCurrency() + "/" +
                                                            CONFIGS_FILE,
                                                    StandardCharsets.UTF_8);

        ChainConfig result = mergeObjects(
                JsonUtils.parseJson(
                        commonConfigs, new TypeReference<ChainConfig>() {
                        }),
                JsonUtils.parseJson(currencyConfigs, new TypeReference<ChainConfig>() {
                }));

        result.setPubSubFullSubscriptionPrefix(result.getPubSubSubscriptionProject() +
                                                       options.getPubSubSubcriptionPrefix().trim());

        result.setCurrency(options.getCurrency());
        result.setTigergraphHost(options.getTigergraphHost());

        System.out.println(result);
        return result;
    }

    public String getTransformNamePrefix() { return currencyCode;}

    public String getPubSubSubscriptionProject() {
        return pubSubSubscriptionProject;
    }

    public String getTransactionsTable() {
        return transactionsTable;
    }

    public List<String> getClickhouseJDBCURIs() {
        return clickhouseJDBCURIs;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRandomClickhouseJDBCURI() {
        return clickhouseJDBCURIs.get(new Random().nextInt(clickhouseJDBCURIs.size()));
    }


    public String getTransactionsTableHot() {
        return transactionsTableHot;
    }


    public String getTigergraphHost() {
        return tigergraphHost;
    }

    public void setTigergraphHost(String tigergraphHost) {
        this.tigergraphHost = tigergraphHost;
    }

    public String getPubSubFullSubscriptionPrefix() {
        return pubSubFullSubscriptionPrefix;
    }

    public void setPubSubFullSubscriptionPrefix(String pubSubFullSubscriptionPrefix) {
        this.pubSubFullSubscriptionPrefix = pubSubFullSubscriptionPrefix;
    }


    public Map<String, Token> getTokensMetadata() {
        return TokensMetadataUtils.parseTokensMetadata(tokensMetadata);
    }

    @Override
    public String toString() {
        return "ChainConfig{" +
                "pubSubSubscriptionProject='" + pubSubSubscriptionProject + '\'' +
                ", pubSubFullSubscriptionPrefix='" + pubSubFullSubscriptionPrefix + '\'' +
                ", currency='" + currency + '\'' +
                ", transactionsTableHot='" + transactionsTableHot + '\'' +
                ", transactionsTable='" + transactionsTable + '\'' +
                ", clickhouseJDBCURIs=" + clickhouseJDBCURIs +
                ", tigergraphHost='" + tigergraphHost + '\'' +
                ", tokensMetadata='" + tokensMetadata + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }
}
