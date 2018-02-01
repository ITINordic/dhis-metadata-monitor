package zw.org.mohcc.dhis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import spark.Spark;
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
                MonitorConfig.MonitorConfigBuilder configBuild
                        = openConfig();

                MonitorConfig conf = configBuild.build();
                configBuild = conf.toBuilder();
                
                /********************************************************************
                 * All configurations that depend on settings from config files should
                 * be done in the block below. Using the partial evalution config files
                 * BEGIN BLOCK
                 * ******************************************************************/
                
                if (cmd.hasOption("p")) {
                    Map<String, String> mailSettings = conf.getMailSettings();
                    configBuild.emailClient(new SendMailImpl(mailSettings));
                }
                
                /********************************************************************
                 * END BLOCK
                 ********************************************************************/
                
                conf = configBuild.build();

                try (InputStream in = new FileInputStream(Paths.get(conf.getJavaLoggingFile()).toFile())) {
                    LogManager.getLogManager().readConfiguration(in);
                    Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.INFO, "Starting DHIS Metadata Monitor...");

                } catch (IOException | SecurityException ex) {
                    Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }
                Spark.ipAddress(conf.getBindAddress());
                DhisMonitorApp dma = new DhisMonitorApp(conf);
                dma.start();
            }
        } catch (ParseException ex) {
            Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }

    private static MonitorConfig.MonitorConfigBuilder openConfig() {
        MonitorConfig.MonitorConfigBuilder configBuild = MonitorConfig.builder();
        Path appHome = Paths.get(System.getProperty("user.home")).resolve(".sadombo");
        Path etcDir = Paths.get("/etc/dhis-metadata-monitor");
        File etcConfig = etcDir.resolve("config.properties").toFile();
        File etcConfigJson = etcDir.resolve("config.json").toFile();
        File homeConfig = appHome.resolve("config.properties").toFile();
        File homeConfigJson = appHome.resolve("config.json").toFile();
        File homeSecret = appHome.resolve("secret.properties").toFile();
        if (etcConfig.exists()) {
            try (InputStream in = new FileInputStream(etcConfig)) {
                Properties properties = new Properties();
                properties.load(in);
                configBuild.addPropertiesConfig(properties);
            } catch (IOException ex) {
                Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (etcConfigJson.exists()) {
            try (InputStream in = new FileInputStream(etcConfigJson)) {
                Properties properties = new Properties();
                properties.load(in);
                configBuild.addPropertiesConfig(properties);
            } catch (IOException ex) {
                Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (homeConfig.exists()) {
            try (InputStream in = new FileInputStream(homeConfig)) {
                Properties properties = new Properties();
                properties.load(in);
                configBuild.addPropertiesConfig(properties);
            } catch (IOException ex) {
                Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (homeConfigJson.exists()) {
            try (InputStream in = new FileInputStream(homeConfigJson)) {
                Properties properties = new Properties();
                properties.load(in);
                configBuild.addPropertiesConfig(properties);
            } catch (IOException ex) {
                Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (homeSecret.exists()) {
            try (InputStream in = new FileInputStream(homeSecret)) {
                Properties properties = new Properties();
                properties.load(in);
                configBuild.addPropertiesConfig(properties);
            } catch (IOException ex) {
                Logger.getLogger(DHISMetaDataMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        configBuild.appHome(appHome)
                .addJsonPathJacksonConfiguration();

        return configBuild;
    }
}
