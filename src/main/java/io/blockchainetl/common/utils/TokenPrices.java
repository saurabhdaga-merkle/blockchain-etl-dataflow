package io.blockchainetl.common.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.blockchainetl.common.tigergraph.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TokenPrices {
    private static final String CRYPTOCOMPARE_API_KEY = "12e40a21ca4933874e0242bf0fde2b5d1f99304ca431dca5d541f3be03c0ef66";

    private static final Logger LOG =
            LoggerFactory.getLogger(TokenPrices.class);

    public static LoadingCache<String, Float> coinPrices = CacheBuilder.newBuilder()
            .expireAfterWrite(120, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Float>() {
                        public Float load(String key) throws Exception {
                            return TokenPrices.makeRequest(key);
                        }
                    });

    private static String formRequestURL(String fromCurrencyCode,
                                         long timestamp) {

        String baseUri = "https://min-api.cryptocompare.com/data/" + "v2" + "/" + "histohour" + "?";
        baseUri += "fsym=" + fromCurrencyCode;
        baseUri += "&tsym=" + "USD";
        baseUri += "&toTs=" + timestamp;
        baseUri += "&e=" + "CCCAGG";
        baseUri += "&limit=" + 1;
        baseUri += "&api_key=" + TokenPrices.CRYPTOCOMPARE_API_KEY;
        LOG.info(baseUri);

        return baseUri;
    }

    private static float makeRequest(String currencyCode) throws Exception {

        try {
            String url = formRequestURL(
                    currencyCode,
                    System.currentTimeMillis()
            );

            String response = Utils.makeGetRequest(url);

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray data = jsonResponse.getJSONObject("Data").getJSONArray("Data");
            float sumOfOpens = 0;
            for (int i = 0; i < data.length(); i++) {
                sumOfOpens += data.getJSONObject(i).getFloat("open");
            }
            return sumOfOpens / data.length();
        } catch (Exception e) {
            LOG.info(e.getMessage());
            throw e;
        }
    }

    public static float get_hourly_price(String currencyCode) throws ExecutionException {
        if (currencyCode.equals("XSGD"))
            return FiatPrices.coinPrices.get(currencyCode);
        else return TokenPrices.coinPrices.get(currencyCode);
    }
}