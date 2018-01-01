/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.monitor;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import zw.mohcc.dhis.JUnitSoftAssertions;
 
/**
 *
 * @author cliffordc
 */
public class MonitorConfigTest {
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

        final MonitorConfig config = MonitorConfig.builder()
                .apiRootUrl(testURL)
                .password(pass)
                .username(test)
                .userHome(root).build();

        softly.assertThat(config)
                .hasApiRootUrl(testURL)
                .hasUsername(test)
                .hasPassword(pass)
                // FIXME: Work around assertj gen treating userHome as if it is a collection 
                .hasFieldOrPropertyWithValue("userHome", root);
        

    }
    
}
