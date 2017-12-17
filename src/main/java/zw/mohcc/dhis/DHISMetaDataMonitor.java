package zw.mohcc.dhis;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import spark.Request;
import spark.Response;
import static spark.Spark.*;

public class DHISMetaDataMonitor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try (InputStream in = DHISMetaDataMonitor.class.getResourceAsStream("/secret.properties")) {
            Properties prop = new Properties();
            prop.load(in);

            get("/hello", (req, res) -> "Hello World");
            // https://zim.dhis2.org/develop/api/categories/ru80fGU70hD.xml?fields=categoryOptions\[name,code,id\]
            get("/monitor", (Request req, Response res) -> {
                final HttpResponse<String> result = Unirest.get("https://zim.dhis2.org/develop/api/{type}/{id}")
                        .routeParam("type", "categories")
                        .routeParam("id", "ru80fGU70hD")
                        .queryString("fields", "categoryOptions[name,code,id]")
                        .basicAuth(prop.getProperty("username"), prop.getProperty("password"))
                        .asString();
                return result.getBody();
            });
        } catch (IOException ex) {
            Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);            
        }

    }
}
