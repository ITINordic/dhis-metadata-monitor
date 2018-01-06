/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.org.mohcc.dhis.apiclient;

import zw.org.mohcc.dhis.apiclient.Field;
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
public class FieldTest {
    
    public FieldTest() {
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

    
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void TestFieldName() {
        final String testField = "field1";
        Field field = Field.builder().name(testField).build();
        softly.assertThat(field).hasName(testField);
    }

    @Test
    public void TestFieldField() {
        final String testField1 = "field1";
        final String testField2 = "field2";

        Field field = Field.builder()
                .name(testField1)
                .beginField()
                .name(testField2)
                .end()
                .build();
        softly.assertThat(field)                
                .hasName(testField1);
        softly.assertThat(field.getFields())
                .extracting("name")
                .containsOnly(testField2);
    }
    
}
