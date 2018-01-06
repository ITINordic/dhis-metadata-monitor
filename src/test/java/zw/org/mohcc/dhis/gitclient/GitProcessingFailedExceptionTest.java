/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.org.mohcc.dhis.gitclient;

import zw.org.mohcc.dhis.gitclient.GitProcessingFailedException;
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
public class GitProcessingFailedExceptionTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    public GitProcessingFailedExceptionTest() {
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

    @Test
    public void testMessage() {
        GitProcessingFailedException gitProcessingFailedException = new GitProcessingFailedException();
        softly.assertThat(gitProcessingFailedException.getMessage())
                .isEqualTo(GitProcessingFailedException.GIT_PROCESSING_FAILED_EXCEPTION_DHIS_MONITOR);
    }

}
