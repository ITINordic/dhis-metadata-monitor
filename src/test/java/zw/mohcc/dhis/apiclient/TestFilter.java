/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.apiclient;

import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author cliffordc
 */
public class TestFilter {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void TestFilterLhs() {
        final String testLhs = "myid";
        Filter filter = Filter.builder().lhs(testLhs).build();
        softly.assertThat(filter)
                .hasLhs(testLhs)
                .hasOp(null)
                .hasNoValues();
    }

    @Test
    public void TestFilterOp() {
        final String testOp = "opeq";
        Filter filter = Filter.builder().op(testOp).build();
        softly.assertThat(filter)
                .hasLhs(null)
                .hasOp(testOp)
                .hasNoValues();
    }

    @Test
    public void TestFilterValue() {
        final String testValue = "value1";
        Filter filter = Filter.builder().value(testValue).build();
        softly.assertThat(filter)
                .hasLhs(null)
                .hasOp(null)
                .hasOnlyValues(testValue);
    }

    @Test
    public void TestFilterDefaults() {
        Filter filter = Filter.builder().build();
        softly.assertThat(filter)
                .hasLhs(null)
                .hasOp(null)
                .hasNoValues();
    }
}
