/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.monitor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import zw.mohcc.dhis.JUnitSoftAssertions;
import zw.mohcc.dhis.apiclient.HttpClient;
import zw.mohcc.dhis.apiclient.HttpClientFactory;

/**
 *
 * @author cliffordc
 */
public class DhisMonitorAppTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    public DhisMonitorAppTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of start method, of class DhisMonitorApp.
     */
    @Test
    public void testMonitor() {
        System.out.println("start");
        final String apiRootUrl = "http://www.example.org";
        final String email1 = "email1@example.org";
        final String list1 = "d1,d2,a1d3,a1d4";
        final String email2 = "email2@example.org";
        final String list2 = "d1,d2,a2d3,a2d4";
        final String pass = "pass";
        final String test = "test";
        final Path root = tmp.newFolder(".sadombo").toPath();
        Properties properties = new Properties();
        properties.setProperty("apiRootUrl", apiRootUrl);
        properties.setProperty("username", test);
        properties.setProperty("password", pass);
        properties.setProperty("dataset.app1.email", email1);
        properties.setProperty("dataset.app1.list", list1);
        properties.setProperty("dataset.app2.email", email2);
        properties.setProperty("dataset.app2.list", list2);

        // "http://www.example.org/DataSets?filter=code:eq:d1&fields=organisationUnits[name,id,code]"
        Pattern pattern = Pattern.compile("http://www.example.org/(?<type>[^/#?:&]+)\\?filter=code:eq:(?<code>[^/#?:&]+).*");
        HttpClientFactory clientFactory = new HttpClientFactory() {
            @Override
            public HttpClient getInstance(String username, String password, Map<String, String> headers, String toURLString) {
                return (HttpClient) () -> {
                    Matcher matcher = pattern.matcher(toURLString);
                    if (matcher.matches()) {
                        final String type = matcher.group("type");
                        final String code = matcher.group("code");
                        final String filename = String.format("/%s-%s.json", type, code);

                        URL url = this.getClass().getResource(filename);
                        if(url == null) {
                            return toURLString;
                        }
                        File file = new File(url.getFile());
                        String json = "";
                        try {
                            json = FileUtils.readFileToString(file);
                        } catch (IOException ex) {
                            Logger.getLogger(DhisMonitorAppTest.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return json;
                    } else {
                        return toURLString;
                    }
                };
            }
        };

        final MonitorConfig config = MonitorConfig.builder()
                .addPropertiesConfig(properties)
                .appHome(root)
                .clientFactory(clientFactory)
                .build();

        DhisMonitorApp instance = new DhisMonitorApp(config);
        String monitor = instance.monitor();
        softly.assertThat(monitor).isEqualTo("");

    }

}
