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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cliffordc
 */
public class DhisMonitorAppTest {
    
    public DhisMonitorAppTest() {
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
     * Test of start method, of class DhisMonitorApp.
     */
    @Test
    public void testStart() {
        System.out.println("start");
        MonitorConfig config = MonitorConfig.builder()
                .apiRootUrl("http://www.example.com")
                .build();
        DhisMonitorApp instance = new DhisMonitorApp(config);
        instance.start();

    }
    
}
