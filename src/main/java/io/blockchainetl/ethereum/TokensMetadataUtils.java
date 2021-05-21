package io.blockchainetl.ethereum;

import com.google.common.collect.Maps;
import io.blockchainetl.ethereum.domain.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TokensMetadataUtils {

    private static final Logger LOG =
            LoggerFactory.getLogger(TransactionsTracesTokensTigerGraphPipeline.class);

    static Token createTokensMetadata(String address, String symbol, String name, int decimals) {
        return new Token(address, symbol, name, decimals);
    }

    public static Map<String, Token> parseTokensMetadata(String tokensMetadata) {
        Map<String, Token> tokensMetadataMap = Maps.newHashMap();

        for (String tokens : tokensMetadata.split(";")) {
            String[] tokens_split = tokens.split(",");
            tokensMetadataMap.put(tokens_split[0], createTokensMetadata(tokens_split[0], tokens_split[1], tokens_split[2], Integer.parseInt(tokens_split[3])));
        }
        return tokensMetadataMap;
    }
}
