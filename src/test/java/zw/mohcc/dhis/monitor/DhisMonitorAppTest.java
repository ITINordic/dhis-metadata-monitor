/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.monitor;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
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
import static zw.mohcc.dhis.monitor.DhisMonitorApp.dhisUniqueObjectId;

/**
 *
 * @author cliffordc
 */
public class DhisMonitorAppTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final String PERIOD_TYPE = "periodType";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String CODE = "code";
    private static final String CATEGORIES = "categories";
    private static final String CATEGORY_COMBO = "categoryCombo";
    private static final String ORGANISATION_UNITS = "organisationUnits";
    private static final String DATA_SET = "dataSets";

    // "http://www.example.org/DataSets?filter=code:eq:d1&fields=organisationUnits[name,id,code]"
    private static final Pattern PATTERN = Pattern.compile("http://www.example.org/(?<type>[^/#?:&]+)\\?filter=code:eq:(?<code>[^/#?:&]+).*");
    private static Map<String, Map<String, Object>> dhisObjects;
    private static Set<String> noFile;

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

//        "dataSets": [
//        {
//            "code": "ATB_002",
//            "name": "ATB - 002 HTS",
//            "id": "DHSN26gmu0F",
//            "periodType": "Monthly",
//            "categoryCombo": {
//                "categories": [
//                    {
//                        "code": "default",
//                        "name": "default"
//                    }
//                ]
//            },
//            "organisationUnits": [
//                {
//                  
    private static Map<String, Object> createDataSet(String code,
            String periodType,
            String categoryCombo,
            String[] organisationUnits) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(CODE, code);
        hashMap.put(NAME, code + " NAME");
        hashMap.put(ID, Base64.getEncoder().encodeToString(code.getBytes()));

        hashMap.put(PERIOD_TYPE, periodType);
        hashMap.put(CATEGORY_COMBO, dhisObjects.get(categoryCombo));
        hashMap.put(ORGANISATION_UNITS, generateListFromString(organisationUnits));
        addDhisObject(postfixString(DATA_SET, code), hashMap);
        return hashMap;
    }

    private static Map<String, Object> createCategoryCombo(String code, String[] categories) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(CATEGORIES, generateListFromString(categories));
        addDhisObject(postfixString(CATEGORY_COMBO, code), hashMap);
        return hashMap;
    }

    private static List<Map<String, Object>> generateListFromString(String[] objectCodes) {
        List<Map<String, Object>> objectList
                = Arrays.stream(objectCodes).map(key -> dhisObjects.get(key)).collect(Collectors.toList());
        return objectList;
    }

    private static String[] postfixStringArray(String postfix, String... objectCodes) {
        for (int i = 0; i < objectCodes.length; i++) {
            objectCodes[i] = postfixString(postfix, objectCodes[i]);
        }
        return objectCodes;
    }

    private static String postfixString(String postfix, String code) {
        return dhisUniqueObjectId(code, postfix);
    }

    private static Map<String, Object> createCategories(String code) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(CODE, code);
        hashMap.put(NAME, code + " NAME");
        hashMap.put(ID, Base64.getEncoder().encodeToString(code.getBytes()));
        addDhisObject(postfixString(CATEGORIES, code), hashMap);
        return hashMap;
    }

    private static Map<String, Object> createOrganisationUnits(String code) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(CODE, code);
        hashMap.put(NAME, code + " NAME");
        hashMap.put(ID, Base64.getEncoder().encodeToString(code.getBytes()));
        addDhisObject(postfixString(ORGANISATION_UNITS, code), hashMap);
        return hashMap;
    }

    /**
     * Test of start method, of class DhisMonitorApp.
     */
    @Test
    public void testMonitor() {
        System.out.println("start");
        final Path root = tmp.newFolder(".sadombo").toPath();
        Properties properties = loadConfigProp();

        HttpClientFactory clientFactory = mockClientFactory();

        final MonitorConfig config = MonitorConfig.builder()
                .addPropertiesConfig(properties)
                .appHome(root)
                .clientFactory(clientFactory)
                .build();

        // commit
        defineObjects();

        DhisMonitorApp instance = new DhisMonitorApp(config);
        Map<String, String> monitor = instance.monitor();
        final Path repo = root.resolve("repo");
        softly.assertThat(repo.resolve(".git")).exists();
        for (String key : dhisObjects.keySet()) {
            if (noFile.contains(key)) {
                continue;
            }
            softly.assertThat(repo.resolve(key + ".json")).exists();
        }
        for (File file : FileUtils.listFilesAndDirs(root.toFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
            System.out.println(file.getPath());
        }
        System.out.println(monitor.values().stream().collect(Collectors.joining("\n\n")));
    }

    @Test
    public void testMonitorDiff() {
        System.out.println("start");
        final Path root = tmp.newFolder(".sadombo").toPath();
        Properties properties = loadConfigProp();

        HttpClientFactory clientFactory = mockClientFactory();

        final MonitorConfig config = MonitorConfig.builder()
                .addPropertiesConfig(properties)
                .appHome(root)
                .clientFactory(clientFactory)
                .build();

        // first commit 
        defineObjects();

        DhisMonitorApp instance = new DhisMonitorApp(config);
        Map<String, String> monitor1 = instance.monitor();
        
        // second commit
        Map<String, Object> cat01 = dhisObjects.get(postfixString(CATEGORIES, "cat01"));
        cat01.put(NAME, "changed name");
        Map<String, String> monitor2 = instance.monitor();

        final Path repo = root.resolve("repo");
        softly.assertThat(repo.resolve(".git")).exists();
        for (String key : dhisObjects.keySet()) {
            if (noFile.contains(key)) {
                continue;
            }
            softly.assertThat(repo.resolve(key + ".json")).exists();
        }
        for (File file : FileUtils.listFilesAndDirs(root.toFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
            System.out.println(file.getPath());
        }
        System.out.println(monitor1.values().stream().collect(Collectors.joining("\n\n")));
        System.out.println(monitor1.keySet().stream().collect(Collectors.joining("\n\n")));
        System.out.println(monitor2.values().stream().collect(Collectors.joining("\n\n")));
        System.out.println(monitor2.keySet().stream().collect(Collectors.joining("\n\n")));
    }

    private Properties loadConfigProp() {
        final String apiRootUrl = "http://www.example.org";
        final String email1 = "email1@example.org";
        final String list1 = "d1,d2,a1d3,a1d4";
        final String email2 = "email2@example.org";
        final String list2 = "d1,d2,a2d3,a2d4";
        final String pass = "pass";
        final String test = "test";
        Properties properties = new Properties();
        properties.setProperty("apiRootUrl", apiRootUrl);
        properties.setProperty("username", test);
        properties.setProperty("password", pass);
        properties.setProperty("dataset.app1.email", email1);
        properties.setProperty("dataset.app1.list", list1);
        properties.setProperty("dataset.app2.email", email2);
        properties.setProperty("dataset.app2.list", list2);
        return properties;
    }

    private static void defineObjects() {
        dhisObjects = new HashMap<>();
        noFile = new HashSet<>();

        createCategories("cat01");
        createCategories("cat02");
        createCategoryCombo("com01", postfixStringArray(CATEGORIES, "cat01", "cat02"));
        noFile.add(postfixString(CATEGORY_COMBO, "com01"));
        createOrganisationUnits("org01");
        createOrganisationUnits("org02");
        createDataSet("d1", "monthly", postfixString(CATEGORY_COMBO, "com01"), postfixStringArray(ORGANISATION_UNITS, "org01", "org02"));
        createDataSet("d2", "monthly", postfixString(CATEGORY_COMBO, "com01"), postfixStringArray(ORGANISATION_UNITS, "org01", "org02"));
        createDataSet("a1d3", "monthly", postfixString(CATEGORY_COMBO, "com01"), postfixStringArray(ORGANISATION_UNITS, "org01", "org02"));
        createDataSet("a1d4", "monthly", postfixString(CATEGORY_COMBO, "com01"), postfixStringArray(ORGANISATION_UNITS, "org01", "org02"));
        createDataSet("a2d3", "monthly", postfixString(CATEGORY_COMBO, "com01"), postfixStringArray(ORGANISATION_UNITS, "org01", "org02"));
        createDataSet("a2d4", "monthly", postfixString(CATEGORY_COMBO, "com01"), postfixStringArray(ORGANISATION_UNITS, "org01", "org02"));
    }

    private static void addDhisObject(String code, Map<String, Object> object) {
        dhisObjects.put(code, object);
    }

    private static HttpClientFactory mockClientFactory() {
        return new HttpClientFactory() {
            @Override
            public HttpClient getInstance(String username, String password, Map<String, String> headers, String toURLString) {
                return (HttpClient) () -> {
                    Matcher matcher = PATTERN.matcher(toURLString);
                    if (matcher.matches()) {
                        final String type = matcher.group("type");
                        final String code = matcher.group("code");
                        final String objectCode = postfixStringArray(type, code)[0];
                        Writer writer = new StringWriter();
                        PebbleEngine engine = new PebbleEngine.Builder().build();
                        PebbleTemplate compiledTemplate;
                        String json = toURLString;
                        try {
                            compiledTemplate = engine.getTemplate(type + ".ptl");
                            compiledTemplate.evaluate(writer, dhisObjects.get(objectCode));
                            json = writer.toString();
                        } catch (PebbleException ex) {
                            Logger.getLogger(DhisMonitorAppTest.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(DhisMonitorAppTest.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return json;
                    }
                    return toURLString;

                };
            }
        };
    }

}
