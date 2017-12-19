/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis;

import org.junit.Test;
import static zw.mohcc.dhis.Assertions.*;
import static org.assertj.core.api.Assertions.*;

public class TestDHISQuery {

    @Test
    public void TestDHISQueryBuilder() {

        final String testURL = "http://example.org";
        final String testId = "qswfwweResdew";
        final String testType = "fwszrfwa4erWRWR";
        final String field1Name = "sffesrfrewrfsd";
        final String field2Name = "sgdsdgsgqew";
        final Field field1 = Field.builder()
                .name(field1Name).build();
        final Field field2 = Field.builder()
                .name(field2Name).build();
        DHISQuery field = DHISQuery.builder()
                .url(testURL)
                .id(testId)
                .type(testType)
                .field(field1)
                .field(field2)
                .build();
        assertThat(field)
                .hasUrl(testURL)
                .hasId(testId)
                .hasType(testType)
                .hasFields(field1, field2);
    }

    @Test
    public void TestDHISQueryToURLString() {
        final Field field1 = Field.builder()
                .name("children").build();
        final Field field2 = Field.builder()
                .name("parent").build();
        DHISQuery query = DHISQuery.builder()
                .url("http://example.com/api")
                .id("100")
                .type("people")
                .field(field1)
                .field(field2)
                .build();
        assertThat(query.toURLString()).isEqualTo("http://example.com/api/people/100?fields=children,parent");
    }

}
