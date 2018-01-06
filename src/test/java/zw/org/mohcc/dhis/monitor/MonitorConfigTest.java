/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.org.mohcc.dhis.monitor;

import zw.org.mohcc.dhis.monitor.MonitorConfig;
import zw.org.mohcc.dhis.monitor.DataSetGroupConfig;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import org.json.JSONWriter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import zw.org.mohcc.dhis.JUnitSoftAssertions;

/**
 *
 * @author cliffordc
 */
public class MonitorConfigTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    public MonitorConfigTest() {
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
     * Test of getApiRootUrl method, of class MonitorConfig.
     */
    @Test
    public void testBuilder() {

        final String testURL = "http://example.org";
        final String pass = "pass";
        final String test = "test";
        final Path root = Paths.get("/tmp");

        final String email1 = "email1@example.org";
        final String list1 = "a1d1,a1d2,a1d3,a1d4";
        final String app1 = "app1";
        DataSetGroupConfig app1DatasetGroup = DataSetGroupConfig.builder()
                .name(app1)
                .email(email1)
                .dataSets(Arrays.asList(list1.split(",")))
                .build();

        final MonitorConfig config = MonitorConfig.builder()
                .apiRootUrl(testURL)
                .password(pass)
                .username(test)
                .appHome(root)
                .beginDataSet()
                .name(app1)
                .email(email1)
                .dataSets(Arrays.asList(list1.split(",")))
                .end()
                .build();

        softly.assertThat(config)
                .hasApiRootUrl(testURL)
                .hasUsername(test)
                .hasPassword(pass)
                // FIXME: Work around assertj gen treating appHome as if it is a collection 
                .hasFieldOrPropertyWithValue("appHome", root)
                .hasOnlyDataSetGroups(app1DatasetGroup);

    }

    /**
     * Test of getApiRootUrl method, of class MonitorConfig.
     */
    @Test
    public void testBuilderJson() {

        final String apiRootUrl = "http://www.example.org";
        final String email1 = "email1@example.org";
        final String list1 = "a1d1,a1d2,a1d3,a1d4";
        final String email2 = "email2@example.org";
        final String list2 = "a2d1,a2d2,a2d3,a2d4";
        final String pass = "pass";
        final String test = "test";
        final Path root = Paths.get("/tmp");
        final StringWriter stringWriter = new StringWriter();
        JSONWriter jsonWriter = new JSONWriter(stringWriter);
        jsonWriter
                .object()
                .key("apiRootUrl").value(apiRootUrl)
                .key("username").value(test)
                .key("password").value(pass)
                .key("dataSetGroups").array()
                .object()
                .key("name").value("app1")
                .key("email").value(email1)
                .key("dataSets")
                .array()
                .value("a1d1")
                .value("a1d2")
                .value("a1d3")
                .value("a1d4")
                .endArray()
                .endObject()
                .object()
                .key("name").value("app2")
                .key("email").value(email2)
                .key("dataSets")
                .array()
                .value("a2d1")
                .value("a2d2")
                .value("a2d3")
                .value("a2d4")
                .endArray()
                .endObject()
                .endArray()
                .key("mail")
                .object()
                .key("smtp")
                .object()
                .key("auth")
                .value("true")
                .endObject()
                .endObject()
                .endObject();

        String json = stringWriter.toString();

        DataSetGroupConfig app1 = DataSetGroupConfig.builder()
                .name("app1")
                .email(email1)
                .dataSets(Arrays.asList(list1.split(",")))
                .build();

        DataSetGroupConfig app2 = DataSetGroupConfig.builder()
                .name("app2")
                .email(email2)
                .dataSets(Arrays.asList(list2.split(",")))
                .build();

        final MonitorConfig config = MonitorConfig.builder()
                .addJsonConfig(json)
                .appHome(root)
                .build();

        softly.assertThat(config)
                .hasApiRootUrl(apiRootUrl)
                .hasUsername(test)
                .hasPassword(pass)
                // FIXME: Work around assertj gen treating appHome as if it is a collection 
                .hasFieldOrPropertyWithValue("appHome", root)
                .hasOnlyDataSetGroups(app1, app2);

        softly.assertThat(config.getMailSettings())
                .containsKey("mail.smtp.auth")
                .containsValue("true");

    }

    /**
     * Test of getApiRootUrl method, of class MonitorConfig.
     */
    @Test
    public void testBuilderProperties() {

        final String apiRootUrl = "http://www.example.org";
        final String email1 = "email1@example.org";
        final String list1 = "a1d1,a1d2,a1d3,a1d4";
        final String email2 = "email2@example.org";
        final String list2 = "a2d1,a2d2,a2d3,a2d4";
        final String pass = "pass";
        final String test = "test";
        final Path root = Paths.get("/tmp");
        Properties properties = new Properties();
        properties.setProperty("apiRootUrl", apiRootUrl);
        properties.setProperty("username", test);
        properties.setProperty("password", pass);
        properties.setProperty("dataset.app1.email", email1);
        properties.setProperty("dataset.app1.list", list1);
        properties.setProperty("dataset.app2.email", email2);
        properties.setProperty("dataset.app2.list", list2);
        properties.setProperty("mail.smtp.auth", "true");

        DataSetGroupConfig app1 = DataSetGroupConfig.builder()
                .name("app1")
                .email(email1)
                .dataSets(Arrays.asList(list1.split(",")))
                .build();

        DataSetGroupConfig app2 = DataSetGroupConfig.builder()
                .name("app2")
                .email(email2)
                .dataSets(Arrays.asList(list2.split(",")))
                .build();

        final MonitorConfig config = MonitorConfig.builder()
                .addPropertiesConfig(properties)
                .appHome(root)
                .build();

        softly.assertThat(config)
                .hasApiRootUrl(apiRootUrl)
                .hasUsername(test)
                .hasPassword(pass)
                // FIXME: Work around assertj gen treating appHome as if it is a collection 
                .hasFieldOrPropertyWithValue("appHome", root)
                .hasOnlyDataSetGroups(app1, app2);
        
        softly.assertThat(config.getMailSettings())
                .containsKey("mail.smtp.auth")
                .containsValue("true");
    }

}
