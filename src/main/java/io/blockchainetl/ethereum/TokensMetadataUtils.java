package io.blockchainetl.ethereum;

import com.google.common.collect.Maps;
import io.blockchainetl.ethereum.domain.Token;
import io.blockchainetl.ethereum.domain.TokensMetadata;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TokensMetadataUtils {

    private static final Logger LOG =
            LoggerFactory.getLogger(TransactionsTracesTokensTigerGraphPipeline.class);
    private static Map<String, Token> tokensMetadata;

    static Token createTokensMetadata(JSONObject metadata) {
        return new Token(metadata.getString("address"),
                metadata.getString("symbol"),
                metadata.getString("name"),
                metadata.getInt("decimals"));
    }

    static void readTokensMetadata()  {
        if(tokensMetadata == null) {
            tokensMetadata = Maps.newHashMap();

            JSONArray metadata = new JSONObject(TokensMetadata.TOKENS_METADATA).getJSONArray("data");
            for (int i = 0; i < metadata.length(); i++) {
                JSONObject tokenJson = metadata.getJSONObject(i);
                tokensMetadata.put(tokenJson.getString("address"), createTokensMetadata(tokenJson));
            }
        }
    }

    public static Token getTokenMetadata(String address) {
        readTokensMetadata();
        return tokensMetadata.get(address);
    }

    public static boolean containsTokenMetadata(String address) {
        readTokensMetadata();
        return tokensMetadata.containsKey(address);
    }
}
