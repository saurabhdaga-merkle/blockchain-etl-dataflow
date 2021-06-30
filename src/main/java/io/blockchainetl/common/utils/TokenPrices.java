package io.blockchainetl.common.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.blockchainetl.common.tigergraph.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TokenPrices {
    private static final String CRYPTOCOMPARE_API_KEY = "9ae5eb1139823033fa6b465f00083ab1fe64aa8768f68cd28a07033d20a25405";

    private static final Boolean DAILY_PRICING = false;

    private static final Logger LOG =
            LoggerFactory.getLogger(TokenPrices.class);

    public static LoadingCache<String, Float> coinPrices = CacheBuilder.newBuilder()
            .expireAfterWrite(180, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Float>() {
                        public Float load(String key) {
                            return TokenPrices.makeRequest(key);
                        }
                    });

    private static String formRequestURL(String fromCurrencyCode,
             long timestamp) {
        String baseUri;
        if(DAILY_PRICING) {
            baseUri = "https://min-api.cryptocompare.com/data/" + "v2" + "/" + "histohour" + "?";
            baseUri += "fsym=" + fromCurrencyCode;
            baseUri += "&tsym=" + "USD";
            baseUri += "&toTs=" + timestamp;
            baseUri += "&e=" + "CCCAGG";
            baseUri += "&limit=" + 1;
        } else {
            baseUri = "https://min-api.cryptocompare.com/data/" + "v2" + "/" + "histoday" + "?";
            baseUri += "fsym=" + fromCurrencyCode;
            baseUri += "&tsym=" + "USD";
            baseUri += "&toTs=" + getYesterdayTimestamp();
            baseUri += "&e=" + "CCCAGG";
            baseUri += "&num_records=" + 1;
        }
        baseUri += "&api_key=" + TokenPrices.CRYPTOCOMPARE_API_KEY;
        LOG.info(baseUri);

        return baseUri;
    }

    private static long getYesterdayTimestamp() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime().getTime();
    }

    private static float makeRequest(String currencyCode)  {
        int maxTries = 5;
        int retryCount = 0;
        while (retryCount++ < maxTries) {
            try {
                String url = formRequestURL(
                        currencyCode,
                        System.currentTimeMillis()
                );

                String response = Utils.makeGetRequest(url);

                JSONObject jsonResponse = new JSONObject(response);
                LOG.info(jsonResponse.toString());
                if (jsonResponse.has("Data")) {
                    JSONArray data = jsonResponse.getJSONObject("Data").getJSONArray("Data");
                    float sumOfOpens = 0;
                    for (int i = 0; i < data.length(); i++) {
                        sumOfOpens += data.getJSONObject(i).getFloat("open");
                    }
                    return sumOfOpens / data.length();
                } else {
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                LOG.info(e.getMessage());
            }
        }
        return 0;
    }


    public static float get_hourly_price(String currencyCode) throws ExecutionException {
        if (currencyCode.equals("XSGD"))
            return FiatPrices.coinPrices.get(currencyCode);
        else return TokenPrices.coinPrices.get(currencyCode);
    }
}