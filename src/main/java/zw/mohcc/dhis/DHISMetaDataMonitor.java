package zw.mohcc.dhis;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import zw.mohcc.dhis.monitor.DhisMonitorApp;
import zw.mohcc.dhis.monitor.MonitorConfig;

public class DHISMetaDataMonitor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args[0].equals("help")) {
            System.out.println("Please read the README.html file in the project root directory");
            return;
        }
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JacksonJsonNodeJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
        
        Path appHome = Paths.get(System.getProperty("user.home")).resolve(".sadombo");
        MonitorConfig.MonitorConfigBuilder config
                = MonitorConfig.builder()
                        .appHome(appHome)
                        .addPropertiesConfig(openConfig(appHome, "secret.properties"))
                        .addPropertiesConfig(openConfig(appHome, "config.properties"));
        DhisMonitorApp dma = new DhisMonitorApp(config.build());
        dma.start();
    }

    private static Properties openConfig(Path configDir, String filename) {
        Properties propTmp = null;
        try (InputStream in = new FileInputStream(configDir.resolve(filename).toFile())) {
            propTmp = new Properties();
            propTmp.load(in);
        } catch (IOException ex) {
            Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return propTmp;
    }
}
