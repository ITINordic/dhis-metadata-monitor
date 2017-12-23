/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.gitclient;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import static org.assertj.core.api.Assertions.fail;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author cliffordc
 */
public class GitClientTest {

    @Rule
    public TemporaryFolder gitDirectory = new TemporaryFolder();

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
        gitClient.process(gitDir);
        fail("experiement with jgit api");

    }

    @Test
    public void testProcessModfied() throws Exception {
        System.out.println("process");
        GitClient gitClient = new GitClient();
        
        final String data1v1 = "hello world, this is a data1 file.\n"
                + "first changing v1\n"
                + "no change\n"
                + "second changing v1\n"
                + "no change\n";
        final String data1v2 = "hello world, this is a data1 file.\n"
                + "first changing v2\n"
                + "no change\n"
                + "second changing v2\n"
                + "no change\n";
        final String data2v1 = "hello world, this is a data2 file.\n"
                + "first changing v1\n"
                + "no change\n"
                + "second changing v1\n"
                + "no change\n";
        final String data2v2 = "hello world, this is a data2 file.\n"
                + "first changing v2\n"
                + "no change\n"
                + "second changing v2\n"
                + "no change\n";
        final Path gitDir = gitDirectory.getRoot().toPath();
        final Path data1path = gitDir.resolve("data1.txt");
        final Path data2path = gitDir.resolve("data2.txt");
        writeStringToFile(data1path, data1v1);
        writeStringToFile(data2path, data2v1);
        gitClient.process(gitDir);
        writeStringToFile(data1path, data1v2);
        writeStringToFile(data2path, data2v2);
        gitClient.process(gitDir);
        fail("experiement with jgit api");

    }

    private void writeStringToFile(Path filepath, String data) throws IOException {
        FileUtils.writeStringToFile(filepath.toFile(), data);
    }

}
