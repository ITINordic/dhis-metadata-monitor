/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis;

import org.junit.Test;

/**
 *
 * @author cliffordc
 */

import static zw.mohcc.dhis.Assertions.*;

public class TestField {
    
    @Test
    public void TestFieldName(){
        final String testField = "field1";
        Field field = Field.builder().name(testField).build();
        assertThat(field).hasName(testField);
    }
    
}
