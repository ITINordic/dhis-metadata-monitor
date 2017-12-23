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
import zw.mohcc.dhis.apiclient.DhisApiClient;
import zw.mohcc.gitclient.GitClient;

public class DHISMetaDataMonitor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Properties propTmp = null;
        Path configDir = Paths.get(System.getProperty("user.home")).resolve(".sadombo");
        try (InputStream in = new FileInputStream(configDir.resolve("secret.properties").toFile())) {
            propTmp = new Properties();
            propTmp.load(in);

        } catch (IOException ex) {
            Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        Properties prop = propTmp;
        get("/hello", (req, res) -> "Hello World");

        // https://zim.dhis2.org/develop/api/categories/ru80fGU70hD.xml?fields=categoryOptions\[name,code,id\]
        get("/monitor", (req, res) -> {
            DhisApiClient dhisClient = new DhisApiClient();
            dhisClient.call(
                    configDir.resolve(Paths.get("repo", "cat.json")),
                    prop.getProperty("username"),
                    prop.getProperty("password"));
            GitClient gitClient = new GitClient();
            gitClient.process(configDir.resolve("repo"));

            return "ok";
        });
    }
}
