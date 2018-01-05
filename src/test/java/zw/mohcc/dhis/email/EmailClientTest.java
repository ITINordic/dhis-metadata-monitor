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

    private Map<String, Object> sentEmail;

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
        String from = "info@example.org";
        String msg = "hello world";
        String subject = "hello";
        String[] toEmails = new String[]{"jane@example.org", "john@example.org"};
        EmailClient instance = new EmailClientImpl();
        instance.sendMessage(from, toEmails, subject, msg);
        softly.assertThat(sentEmail)
                .containsKeys("from", "subject", "message", "recipients")
                .containsValues(from, subject, msg, toEmails);
    }


    public class EmailClientImpl implements EmailClient {

        @Override
        public void sendMessage(String from, String[] recipients, String subject, String message) {
            sentEmail.put("from", from);
            sentEmail.put("subject", subject);
            sentEmail.put("message", message);
            sentEmail.put("recipients", recipients);
        }
    }

}
