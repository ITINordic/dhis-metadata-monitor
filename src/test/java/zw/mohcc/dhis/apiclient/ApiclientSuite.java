/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.apiclient;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author cliffordc
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({zw.mohcc.dhis.apiclient.DHISQueryTest.class, zw.mohcc.dhis.apiclient.FieldTest.class, zw.mohcc.dhis.apiclient.FilterTest.class, zw.mohcc.dhis.apiclient.HttpClientFactoryTest.class, zw.mohcc.dhis.apiclient.OkHttpClientFactoryTest.class, zw.mohcc.dhis.apiclient.HttpClientTest.class})
public class ApiclientSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
