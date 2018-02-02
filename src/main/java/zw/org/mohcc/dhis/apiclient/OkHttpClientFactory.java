/* 
 * Copyright (c) 2018, ITINordic
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package zw.org.mohcc.dhis.apiclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Value;
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
        builder.url(toURLString);
        Request request = builder.build();
        OkHttpClient okHttpClient = new OkHttpClient(request);
        return okHttpClient;
    }

    public class OkHttpClient implements HttpClient {

        private OkHttpClient(Request request) {
            this.request = request;
        }

        private final Request request;

        @Override
        public String call() {
            // Get metadata
            Response response;
            try {
                response = client.newCall(request).execute();
                final String result = response.body().string();
                return result == null ? "" : result;
            } catch (IOException ex) {
                Logger.getLogger(OkHttpClient.class.getName()).log(Level.SEVERE, request.toString(), ex);
            }
            return null;
        }
    }

    public static String getBasicAuthorization(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
