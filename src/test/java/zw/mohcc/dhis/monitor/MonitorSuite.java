/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.monitor;

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
@Suite.SuiteClasses({zw.mohcc.dhis.monitor.MonitorConfigTest.class, zw.mohcc.dhis.monitor.DhisMonitorAppTest.class, zw.mohcc.dhis.monitor.DataSetGroupConfigTest.class})
public class MonitorSuite {

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
