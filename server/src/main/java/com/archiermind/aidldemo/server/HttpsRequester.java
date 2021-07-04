package com.archiermind.aidldemo.server;

import androidx.annotation.Nullable;
import com.archiermind.aidldemo.Callback;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpsRequester {
    private String requestMethod;
    private String url;
    private Callback callback;

    private Map parameters;

    private HttpsRequester(Builder builder) {
        this.requestMethod = builder.requestMethod;
        this.parameters = builder.parameters;
        this.url = builder.url;
        this.callback = builder.callback;
    }

    public Callback getCallback() {
        return callback;
    }
    public String getUrl() {
        return url;
    }

    public static class Builder {
        private String requestMethod = "GET";
        private String url;
        private Map parameters = new HashMap();
        private Callback callback;

        public Builder(String url) {
            this.url = url;
        }

        public Builder requestMethod(String requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        public Builder callback(Callback callback){
            this.callback = callback;
            return this;
        }

        public Builder parameters(Map parameters) {
            this.parameters = parameters;
            return this;
        }

        public HttpsRequester build() {
            return new HttpsRequester(this);
        }

    }

    /**
     * should be request in thread
     */
    public String send() {

        OutputStream outputStream = null;
        InputStream inputStream = null;
        HttpsURLConnection connection = null;
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(this.url);

            connection = (HttpsURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(10 * 1000);
            connection.connect();

            String parameters = sequenceParameters(this.parameters);
            outputStream = connection.getOutputStream();
            if (parameters != null && !parameters.isEmpty()) {
                outputStream.write(parameters.getBytes("UTF-8"));
                outputStream.flush();
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                    result.append("\n");
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }
        return result.toString();

    }

    public String sequenceParameters(@Nullable Map parameters) {
        if (parameters == null) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        Set<String> keys = parameters.keySet();
        for (String key : keys) {
            result.append(key);
            result.append("=");
            result.append(parameters.get(key));
            result.append("&");
        }
        String s = result.toString();
        if (s.isEmpty()) {
            return null;
        }

        return s.substring(0, s.length() - 1);
    }


}
