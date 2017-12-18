package zw.mohcc.dhis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            DHISQuery.builder()
                    .url("https://zim.dhis2.org/develop/api/")
                    .type("categories")
                    .accept("application/json")
                    .id("ru80fGU70hD")
                    .field(Field.builder().name("categoryOptions").build());
        } catch (IOException ex) {
            Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);            
        }

    }
}
