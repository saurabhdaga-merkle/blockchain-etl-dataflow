package io.blockchainetl.common.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.blockchainetl.common.tigergraph.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class FiatPrices {
    private static final String CURRENCYLAYER_API_KEY = "3395daa1e8a1d505f1ba01f8e4edcbd6";
    private static final Logger LOG =
            LoggerFactory.getLogger(FiatPrices.class);
    public static LoadingCache<String, Float> coinPrices = CacheBuilder.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .build(
                    new CacheLoader<String, Float>() {
                        public Float load(String key) throws Exception {
                            return FiatPrices.makeRequest(key);
                        }
                    });

    private static String formRequestURL(String toCurrencyCode) {

        String baseUri = "http://api.currencylayer.com/live?";
        baseUri += "&currencies=" + toCurrencyCode;
        baseUri += "&format=" + 1;
        baseUri += "&access_key=" + FiatPrices.CURRENCYLAYER_API_KEY;
        LOG.info(baseUri);

        return baseUri;
    }

    public static Float makeRequest(String currencyCode) throws Exception {
        try {
            String url = null;
            if (currencyCode.equals("XSGD")) {
                String fiatCurrencyCode = "SGD";
                url = formRequestURL(
                        "SGD"
                );

                String response = Utils.makeGetRequest(url);
                JSONObject jsonResponse = new JSONObject(response);
                LOG.info(jsonResponse.toString());
                float price = 1 / jsonResponse.getJSONObject("quotes").getLong("USD" + fiatCurrencyCode);
                return price;
            }
        } catch (Exception e) {
            LOG.info(e.getMessage());
            throw e;
        }
        return 0.0f;
    }
}
