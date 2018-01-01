/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.apiclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *
 * @author cliffordc
 */
public class OkHttpClientFactory implements HttpClientFactory {

    public OkHttpClientFactory() {
        this(new okhttp3.OkHttpClient());
    }

    okhttp3.OkHttpClient client;

    public OkHttpClientFactory(okhttp3.OkHttpClient client) {
        this.client = client;
    }

    @Override
    public HttpClient getInstance(String username, String password, Map<String, String> headers, String toURLString) {
        final Request.Builder builder = new Request.Builder();
        builder.addHeader(username, password);
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
        }
        final String basicAuth = getBasicAuthorization(username, password);
        builder.addHeader("Authorization", basicAuth);
        Request request = builder.build();
        return new OkHttpClient(client, request);
    }

    public static class OkHttpClient implements HttpClient {

        private final okhttp3.OkHttpClient client;

        private OkHttpClient(okhttp3.OkHttpClient client, Request request) {
            this.request = request;
            this.client = client;
        }

        private final Request request;

        @Override
        public String call() {
            // Get metadata
            Response response;
            try {
                response = client.newCall(request).execute();
                ObjectMapper mapper = new ObjectMapper();
                final ResponseBody body = response.body();
                if (body != null) {
                    Object json = mapper.readValue(body.string(), Object.class);
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
                }
            } catch (IOException ex) {
                Logger.getLogger(OkHttpClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    public static String getBasicAuthorization(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
