/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.apiclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author cliffordc
 */
public class DhisApiClient {

    DHISQuery.DHISQueryBuilder queryBuilder;
    Request.Builder requestBuilder;

    public void call(Path catFile, String username, String password) {
        // Contruct request metadata
        Request request = DHISQuery.builder()
                .url("https://zim.dhis2.org/develop/api")
                .username(username)
                .password(password)
                .type("categories")
                .accept("application/json")
                .beginField()
                .name("categoryOptions")
                .beginField()
                .name("name")
                .end()
                .end()
                .build().toHttpClient();

        // Get metadata
        OkHttpClient client = new OkHttpClient();
        Response response;
        try {
            response = client.newCall(request).execute();
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(response.body().string(), Object.class);
            String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);

            FileUtils.writeStringToFile(catFile.toFile(), indented);
            
        } catch (IOException ex) {
            Logger.getLogger(DhisApiClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
