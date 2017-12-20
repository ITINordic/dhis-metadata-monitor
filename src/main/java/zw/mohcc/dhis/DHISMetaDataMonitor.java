package zw.mohcc.dhis;

import zw.mohcc.dhis.apiclient.DHISQuery;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.*;

public class DHISMetaDataMonitor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Path configDir = Paths.get(System.getProperty("user.home")).resolve(".sadombo");
        try (InputStream in = new FileInputStream(configDir.resolve("secret.properties").toFile())) {
            Properties prop = new Properties();
            prop.load(in);

            get("/hello", (req, res) -> "Hello World");
            // https://zim.dhis2.org/develop/api/categories/ru80fGU70hD.xml?fields=categoryOptions\[name,code,id\]
            get("/monitor", (req, res) -> {
                return DHISQuery.builder()
                        .url("https://zim.dhis2.org/develop/api")
                        .type("categories")
                        .accept("application/json")
                        .beginFilter()
                        .lhs("code")
                        .op("eq")
                        .value("0")
                        .end()
                        .beginField()
                        .name("categoryOptions")
                        .end()
                        .build().toURLString();
            });
        } catch (IOException ex) {
            Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
