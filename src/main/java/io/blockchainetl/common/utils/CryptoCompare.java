package io.blockchainetl.common.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CryptoCompare {
    private static final String CRYPTOCOMPARE_API_KEY = "12e40a21ca4933874e0242bf0fde2b5d1f99304ca431dca5d541f3be03c0ef66";

    private static final Logger LOG =
            LoggerFactory.getLogger(CryptoCompare.class);

    static LoadingCache<String, Long> coin_prices = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Long>() {
                        public Long load(String key) throws Exception {
                            return makeRequest(key);
                        }
                    });

    private static String formRequestURL(String resource,
                                         String from_currency_code,
                                         String to_currency_code,
                                         int timestamp,
                                         String access_token,
                                         String exchange_code,
                                         int num_records,
                                         String api_version) throws MalformedURLException, URISyntaxException {

        String base_uri = "https://min-api.cryptocompare.com/data/" + api_version + "/" + resource + "?";
        base_uri += "fsym=" + from_currency_code;
        base_uri += "&tsym=" + to_currency_code;
        base_uri += "&toTs=" + timestamp;
        base_uri += "&e=" + exchange_code;
        base_uri += "&limit=" + num_records;
        base_uri += "&api_key=" + access_token;
        System.out.print(base_uri);

        return base_uri;
    }

    public static Long makeRequest(String currency_code) throws Exception {

        try {
            String url = formRequestURL(
                    "histohour",
                    currency_code,
                    "USD",
                    (int) System.currentTimeMillis(),
                    CRYPTOCOMPARE_API_KEY,
                    "CCCAGG",
                    1,
                    "v2"

            );
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("GET");

            BufferedReader iny =
                    new BufferedReader(new InputStreamReader(con.getInputStream()));
            String output;
            StringBuffer response = new StringBuffer();

            while ((output = iny.readLine()) != null) {
                response.append(output);
            }
            iny.close();

            LOG.info(response.toString());
            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
                LOG.info("Response Code : " + responseCode);
                //printing result from response
                LOG.info(response.toString());
                throw new Exception("Tigergraph error");
            }
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray data = jsonResponse.getJSONObject("Data").getJSONArray("Data");
            long sumOfOpens = 0;
            for (int i = 0; i < data.length(); i++) {
                sumOfOpens += data.getJSONObject(i).getInt("open");
            }
            return sumOfOpens / data.length();
        } catch (MalformedURLException e) {
            LOG.info(e.getMessage());
            throw e;
        } catch (IOException e) {
            LOG.info(e.getMessage());
            throw e;
        } catch (Exception e) {
            LOG.info(e.getMessage());
            throw e;
        }
    }

    public static Long get_hourly_price(String currency_code) throws ExecutionException {
        return coin_prices.get(currency_code);
    }
}
