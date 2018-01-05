/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.email;

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
import zw.mohcc.dhis.JUnitSoftAssertions;

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
