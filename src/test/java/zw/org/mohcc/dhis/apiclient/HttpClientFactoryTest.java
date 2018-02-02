/* 
 * Copyright (c) 2018, ITINordic
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
