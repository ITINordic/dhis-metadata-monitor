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
package zw.org.mohcc.dhis.email;

import zw.org.mohcc.dhis.email.EmailClient;
import java.util.HashMap;
import java.util.Map;
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
