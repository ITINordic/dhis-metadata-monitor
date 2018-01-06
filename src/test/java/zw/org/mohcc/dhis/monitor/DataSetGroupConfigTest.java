/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.org.mohcc.dhis.monitor;

import zw.org.mohcc.dhis.monitor.DataSetGroupConfig;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import zw.org.mohcc.dhis.JUnitSoftAssertions;

/**
 *
 * @author cliffordc
 */
public class DataSetGroupConfigTest {
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    
    public DataSetGroupConfigTest() {
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
     * Test of builder method, of class DataSetGroupConfig.
     */
    @Test
    public void testBuilder() {
        System.out.println("builder");
        final String test = "test";
        final String testemail = "test@example.com";
        final String dataset1 = "dataset1";
        final String dataset2 = "dataset2";
        DataSetGroupConfig dataSetGroup = DataSetGroupConfig.builder()
                .name(test)
                .email(testemail)
                .dataSet(dataset1)
                .dataSet(dataset2)
                .build();

        softly.assertThat(dataSetGroup)
                .hasName(test)
                .hasEmail(testemail)
                .hasOnlyDataSets(dataset1, dataset2);
    }    
}
