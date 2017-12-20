/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.apiclient;

import zw.mohcc.dhis.apiclient.Field;
import zw.mohcc.dhis.apiclient.DHISQuery;
import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;

public class TestDHISQuery {

    @Before
    public void ResetDefaultQuery() {
        DHISQuery.setDefault(null);
    }

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

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
        softly.assertThat(field)
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
        softly.assertThat(query.toURLString()).isEqualTo("http://example.com/api/people/100?fields=children,parent");
    }

    @Test
    public void TestDHISQueryBuilderToURLString() {

        DHISQuery query = DHISQuery.builder()
                .url("http://example.com/api")
                .id("100")
                .type("people")
                .beginField()
                .name("children")
                .beginField()
                .name("age")
                .end()
                .end()
                .beginField()
                .name("parent")
                .end()
                .build();

        softly.assertThat(query.toURLString()).isEqualTo("http://example.com/api/people/100?fields=children[age],parent");
    }

    @Test
    public void TestDHISQueryBuilderDefaults() {

        DHISQuery defaultQuery = DHISQuery.builder()
                .url("http://example.com/api")
                .id("100")
                .type("people")
                .beginField()
                .name("children")
                .beginField()
                .name("age")
                .end()
                .end()
                .beginField()
                .name("parent")
                .end()
                .build();

        DHISQuery.setDefault(defaultQuery);
        DHISQuery query = DHISQuery.builder().build();

        softly.assertThat(query.toURLString()).isEqualTo("http://example.com/api/people/100?fields=children[age],parent");
    }

    @Test
    public void TestDHISQueryBuilderMissingProperties() {

        DHISQuery query = DHISQuery.builder()
                // .url("http://example.com/api")
                .id("100")
                .type("people")
                .beginField()
                .name("children")
                .beginField()
                .name("age")
                .end()
                .end()
                .beginField()
                .name("parent")
                .end()
                .build();

        softly.assertThat(query.toURLString()).isEqualTo("http://uninitialised.example.com/people/100?fields=children[age],parent");

        DHISQuery missingUrl = DHISQuery.builder()
                // .url("http://example.com/api")
                .id("100")
                .type("people")
                .beginField()
                .name("children")
                .beginField()
                .name("age")
                .end()
                .end()
                .beginField()
                .name("parent")
                .end()
                .build();

        softly.assertThat(missingUrl.toURLString()).isEqualTo("http://uninitialised.example.com/people/100?fields=children[age],parent");

        DHISQuery missingType = DHISQuery.builder()
                .url("http://example.com/api")
                .id("100")
                // .type("people")
                .beginField()
                .name("children")
                .beginField()
                .name("age")
                .end()
                .end()
                .beginField()
                .name("parent")
                .end()
                .build();

        softly.assertThat(missingType.toURLString()).isEqualTo("http://example.com/api/dataSets/100?fields=children[age],parent");

        DHISQuery missingId = DHISQuery.builder()
                .url("http://example.com/api")
                // .id("100")
                .type("people")
                .beginField()
                .name("children")
                .beginField()
                .name("age")
                .end()
                .end()
                .beginField()
                .name("parent")
                .end()
                .build();

        softly.assertThat(missingId.toURLString()).isEqualTo("http://example.com/api/people?fields=children[age],parent");

        DHISQuery missingFields = DHISQuery.builder()
                .url("http://example.com/api")
                .id("100")
                .type("people")
                .build();
        softly.assertThat(missingFields.toURLString()).isEqualTo("http://example.com/api/people/100");
    }
}
