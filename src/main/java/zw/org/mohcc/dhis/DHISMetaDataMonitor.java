package zw.org.mohcc.dhis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import zw.org.mohcc.dhis.email.SendMailImpl;
import zw.org.mohcc.dhis.monitor.DhisMonitorApp;
import zw.org.mohcc.dhis.monitor.MonitorConfig;

public class DHISMetaDataMonitor {
    // TODO: 1. Add tests for email configuration
    // TODO: 2. Use buffered read and write to improve speed 
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            // create Options object
            Options options = new Options();

            // add t option
            options.addOption("h", "help", false, "display help text");
            options.addOption("p", "prod", false, "production send emails configure smtp server");

            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("DHIS Monitor", options);
            } else {

                Path appHome = Paths.get(System.getProperty("user.home")).resolve(".sadombo");
                MonitorConfig.MonitorConfigBuilder configBuild
                        = MonitorConfig.builder()
                                .appHome(appHome)
                                .addPropertiesConfig(openConfig(appHome, "secret.properties"))
                                .addPropertiesConfig(openConfig(appHome, "config.properties"))
                                .addJsonPathJacksonConfiguration();
                if (cmd.hasOption("p")) {
                    final MonitorConfig conf = configBuild.build();
                    final Map<String, String> mailSettings = conf.getMailSettings();
                    configBuild = conf.toBuilder();
                    configBuild.emailClient(new SendMailImpl(mailSettings));
                }
                DhisMonitorApp dma = new DhisMonitorApp(configBuild.build());
                dma.start();
            }
        } catch (ParseException ex) {
            Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
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
