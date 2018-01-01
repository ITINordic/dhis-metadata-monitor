package zw.mohcc.dhis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import zw.mohcc.dhis.monitor.DhisMonitorApp;
import zw.mohcc.dhis.monitor.MonitorConfig;

public class DHISMetaDataMonitor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Path userHome = Paths.get(System.getProperty("user.home")).resolve(".sadombo");
        MonitorConfig.MonitorConfigBuilder config
                = MonitorConfig.builder()
                        .userHome(userHome)
                        .addPropertiesConfig(openConfig(userHome, "secret.properties"));
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
