/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author cliffordc
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({zw.mohcc.dhis.DhisSuite.class, 
    zw.mohcc.dhis.gitclient.GitclientSuite.class, 
    zw.mohcc.dhis.monitor.MonitorSuite.class,
    zw.mohcc.dhis.email.EmailSuite.class
})
public class MohccSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
