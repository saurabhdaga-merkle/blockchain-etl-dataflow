package io.blockchainetl.common.tigergraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Utils {

    private static final Logger LOG =
            LoggerFactory.getLogger(Utils.class);

    public static void tigerGraphPost(String[] tigergraphHosts, String chain, String data, String job) throws Exception {

        for (int i = 0; i < tigergraphHosts.length; i++) {
            String url = tigergraphHosts[i] + "/ddl/" +
                    chain + "?&" +
                    "tag=" + job + "&" +
                    "filename=f1&" +
                    "sep=,&" +
                    "eol=\\n";
            try {
                LOG.info(url);
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(data);
                wr.flush();
                wr.close();

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
                    LOG.info(data);
                    LOG.info("Response Code : " + responseCode);
                    //printing result from response
                    LOG.info(response.toString());
                    throw new Exception("Tigergraph error");
                }
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
    }


}
