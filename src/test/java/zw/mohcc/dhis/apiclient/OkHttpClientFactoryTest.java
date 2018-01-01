/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.apiclient;

import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cliffordc
 */
public class OkHttpClientFactoryTest {
    
    public OkHttpClientFactoryTest() {
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
     * Test of getInstance method, of class OkHttpClientFactory.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        String username = "test";
        String password = "pass";
        Map<String, String> headers = null;
        String toURLString = "http://www.example.com";
        OkHttpClientFactory instance = new OkHttpClientFactory();
        HttpClient expResult = null;
        // FIXME: Mock okhttp3 calls
        // HttpClient result = instance.getInstance(username, password, headers, toURLString);
    }

    /**
     * Test of getBasicAuthorization method, of class OkHttpClientFactory.
     */
    @Test
    public void testGetBasicAuthorization() {
        System.out.println("getBasicAuthorization");
        String username = "test";
        String password = "pass";
        String expResult = "Basic dGVzdDpwYXNz";
        String result = OkHttpClientFactory.getBasicAuthorization(username, password);
        assertEquals(expResult, result);
    }
    
}
