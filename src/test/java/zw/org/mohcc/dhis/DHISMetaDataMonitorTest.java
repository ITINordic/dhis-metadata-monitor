/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.org.mohcc.dhis;

import zw.org.mohcc.dhis.DHISMetaDataMonitor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author cliffordc
 */
public class DHISMetaDataMonitorTest {
    
    public DHISMetaDataMonitorTest() {
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
     * Test of main method, of class DHISMetaDataMonitor.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = new String[]{"-h"};
        DHISMetaDataMonitor.main(args);
    }
    
}