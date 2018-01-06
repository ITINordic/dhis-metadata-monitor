/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.org.mohcc.dhis.apiclient;

import zw.org.mohcc.dhis.apiclient.HttpClientFactory;
import zw.org.mohcc.dhis.apiclient.HttpClient;
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
public class HttpClientFactoryTest {

    public HttpClientFactoryTest() {
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
     * Test of getInstance method, of class HttpClientFactory.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        String username = "";
        String password = "";
        Map<String, String> headers = null;
        String toURLString = "";
        HttpClientFactory instance = new HttpClientFactoryImpl();
        String expResult = new StringBuilder()
                            .append(username)
                            .append(password)
                            .append(headers)
                            .append(toURLString)
                            .toString();
        HttpClient result = instance.getInstance(username, password, headers, toURLString);
        assertEquals(expResult, result.call());
    }

    public class HttpClientFactoryImpl implements HttpClientFactory {

        public HttpClient getInstance(String username, String password, Map<String, String> headers, String toURLString) {
            return new HttpClient() {
                @Override
                public String call() {
                        
                    return new StringBuilder()
                            .append(username)
                            .append(password)
                            .append(headers)
                            .append(toURLString)
                            .toString();
                }
            };
        }
    }

}
