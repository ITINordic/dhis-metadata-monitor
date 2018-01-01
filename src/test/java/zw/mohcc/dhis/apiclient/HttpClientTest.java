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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cliffordc
 */
public class HttpClientTest {
    
    public HttpClientTest() {
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
     * Test of call method, of class HttpClient.
     */
    @Test
    public void testCall() {
        System.out.println("call");
        HttpClient instance = new HttpClientImpl();
        String expResult = "HttpClientImpl";
        String result = instance.call();
        assertEquals(expResult, result);
    }

    public class HttpClientImpl implements HttpClient {

        public String call() {
            return "HttpClientImpl";
        }
    }
    
}
