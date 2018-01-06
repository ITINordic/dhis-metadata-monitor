/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.org.mohcc.dhis.gitclient;

import zw.org.mohcc.dhis.gitclient.GitClient;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.StringAssert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import zw.mohcc.dhis.JUnitSoftAssertions;

/**
 *
 * @author cliffordc
 */
public class GitClientTest {

    @Rule
    public TemporaryFolder gitDirectory = new TemporaryFolder();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    public GitClientTest() {
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
     * Test of process method, of class GitClient.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testProcessNoChange() throws Exception {
        System.out.println("process");
        GitClient gitClient = new GitClient();
        final String data1v1 = "hello world, this is a data1 file.\n"
                + "no change\n"
                + "no change\n";
        final String data1v2 = "hello world, this is a data1 file.\n"
                + "no change\n"
                + "no change\n";
        final String data2v1 = "hello world, this is a data2 file.\n"
                + "no change\n"
                + "no change\n";
        final String data2v2 = "hello world, this is a data2 file.\n"
                + "no change\n"
                + "no change\n";
        final Path gitDir = gitDirectory.getRoot().toPath();
        final Path data1path = gitDir.resolve("data1.txt");
        final Path data2path = gitDir.resolve("data2.txt");
        writeStringToFile(data1path, data1v1);
        writeStringToFile(data2path, data2v1);
        gitClient.process(gitDir);
        writeStringToFile(data1path, data1v2);
        writeStringToFile(data2path, data2v2);
        String process = gitClient.process(gitDir);
        softly.assertThat(process).isEqualTo("");

    }

    @Test
    public void testProcessModfied() throws Exception {
        System.out.println("process");
        GitClient gitClient = new GitClient();

        final String data1v1 = "hello world, this is a data1 file.\n"
                + "first changing d1v1\n"
                + "no change\n"
                + "second changing d1v1\n"
                + "no change\n";
        final String data1v2 = "hello world, this is a data1 file.\n"
                + "first changing d1v2\n"
                + "no change\n"
                + "second changing d1v2\n"
                + "no change\n";
        final String data2v1 = "hello world, this is a data2 file.\n"
                + "first changing d2v1\n"
                + "no change\n"
                + "second changing d2v1\n"
                + "no change\n";
        final String data2v2 = "hello world, this is a data2 file.\n"
                + "first changing d2v2\n"
                + "no change\n"
                + "second changing d2v2\n"
                + "no change\n";
        final Path gitDir = gitDirectory.getRoot().toPath();
        final Path data1path = gitDir.resolve("data1.txt");
        final Path data2path = gitDir.resolve("data2.txt");
        writeStringToFile(data1path, data1v1);
        writeStringToFile(data2path, data2v1);
        gitClient.process(gitDir);
        writeStringToFile(data1path, data1v2);
        writeStringToFile(data2path, data2v2);
        String process = gitClient.process(gitDir);
        
// Sample result
//        String diff = "diff --git old/data1.txt new/data1.txt\n"
//                + "index 4fa2dfe..f306177 100644\n"
//                + "--- old/data1.txt\n"
//                + "+++ new/data1.txt\n"
//                + "@@ -1,5 +1,5 @@\n"
//                + " hello world, this is a data1 file.\n"
//                + "-first changing v1\n"
//                + "+first changing v2\n"
//                + " no change\n"
//                + "-second changing v1\n"
//                + "+second changing v2\n"
//                + " no change\n"
//                + "diff --git old/data2.txt new/data2.txt\n"
//                + "index 1c2daa3..410093d 100644\n"
//                + "--- old/data2.txt\n"
//                + "+++ new/data2.txt\n"
//                + "@@ -1,5 +1,5 @@\n"
//                + " hello world, this is a data2 file.\n"
//                + "-first changing v1\n"
//                + "+first changing v2\n"
//                + " no change\n"
//                + "-second changing v1\n"
//                + "+second changing v2\n"
//                + " no change\n";
       
        softly.assertThat(process).contains("--- old/data1.txt");
        softly.assertThat(process).contains("+++ new/data1.txt");
        softly.assertThat(process).contains("--- old/data2.txt");
        softly.assertThat(process).contains("+++ new/data2.txt");

        softly.assertThat(process).contains("-first changing d1v1");
        softly.assertThat(process).contains("+first changing d1v2");
        softly.assertThat(process).contains("-second changing d1v1");
        softly.assertThat(process).contains("+second changing d1v2");

        softly.assertThat(process).contains("-first changing d2v1");
        softly.assertThat(process).contains("+first changing d2v2");
        softly.assertThat(process).contains("-second changing d2v1");
        softly.assertThat(process).contains("+second changing d2v2");

    }

    private void writeStringToFile(Path filepath, String data) throws IOException {
        FileUtils.writeStringToFile(filepath.toFile(), data);
    }

}
