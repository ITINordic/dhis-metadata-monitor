/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.apiclient;

import org.junit.Rule;
import org.junit.Test;
import zw.mohcc.dhis.JUnitSoftAssertions;

/**
 *
 * @author cliffordc
 */
public class TestField {

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
