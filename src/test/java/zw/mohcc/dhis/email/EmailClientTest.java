/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.email;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import zw.mohcc.dhis.JUnitSoftAssertions;

/**
 *
 * @author cliffordc
 */
public class EmailClientTest {

    private Map<String, String> sentEmail;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    public EmailClientTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        sentEmail = new HashMap<>();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of sendEmail method, of class EmailClient.
     */
    @Test
    public void testSendEmail() {
        System.out.println("sendEmail");
        String email = "user@example.org";
        String msg = "hello";
        EmailClient instance = new EmailClientImpl();
        instance.sendEmail(email, msg);
        final Map.Entry<String, String> emailEntry = createEmailEntry(email, msg);
        softly.assertThat(sentEmail).containsOnly(emailEntry);
    }

    private static Map.Entry<String, String> createEmailEntry(String email, String msg) {
        return new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return email;
            }

            @Override
            public String getValue() {
                return msg;
            }

            @Override
            public String setValue(String value) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    public class EmailClientImpl implements EmailClient {

        public void sendEmail(String email, String msg) {
            sentEmail.put(email, msg);
        }
    }

}
