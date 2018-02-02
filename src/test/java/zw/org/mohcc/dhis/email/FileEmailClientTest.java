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

import zw.org.mohcc.dhis.email.FileEmailClient;
import java.io.File;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import zw.org.mohcc.dhis.JUnitSoftAssertions;

/**
 *
 * @author cliffordc
 */
public class FileEmailClientTest {

    @Rule
    public TemporaryFolder emailDirectory = new TemporaryFolder();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    public FileEmailClientTest() {
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
     * Test of sendMessage method, of class FileEmailClient.
     */
    @Test
    public void testSendMessage() {
        System.out.println("sendMessage");
        String from = "info@example.org";
        String[] recipients = new String[]{"jane@example.org", "john@example.org"};
        String subject = "Test";
        String message = "hello world";
        final Path emailRoot = emailDirectory.newFolder("email").toPath();
        FileEmailClient instance = new FileEmailClient(emailRoot);
        instance.sendMessage(from, recipients, subject, message);
        final Path emailDir = emailRoot.resolve("jane@example.org_john@example.org");
        softly.assertThat(emailDir).exists();
        softly.assertThat(emailDir.toFile().list()).hasSize(1);

        for (File file : FileUtils.listFilesAndDirs(emailRoot.toFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
            System.out.println(file.getPath());
        }
    }

}
