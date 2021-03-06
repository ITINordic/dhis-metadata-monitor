/* 
 * Copyright (c) 2018, ITINordic
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package zw.org.mohcc.dhis.apiclient;

import zw.org.mohcc.dhis.apiclient.Field;
import zw.org.mohcc.dhis.apiclient.DHISQuery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import zw.org.mohcc.dhis.JUnitSoftAssertions;

/**
 *
 * @author cliffordc
 */
public class DHISQueryTest {

    public DHISQueryTest() {
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
        softly.assertThat(query.toURLString()).isEqualTo("http://example.com/api/people/100?paging=false&fields=children,parent");
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

        softly.assertThat(query.toURLString()).isEqualTo("http://example.com/api/people/100?paging=false&fields=children[age],parent");
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

        DHISQuery query = defaultQuery.toBuilder().build();

        softly.assertThat(query.toURLString()).isEqualTo("http://example.com/api/people/100?paging=false&fields=children[age],parent");
    }

    @Test
    public void TestDHISQueryBuilderPaging() {

        DHISQuery query = DHISQuery.builder()
                .url("http://example.com/api")
                .id("100")
                .type("people")
                .build();

        softly.assertThat(query.toURLString()).isEqualTo("http://example.com/api/people/100?paging=false");

        query = DHISQuery.builder()
                .url("http://example.com/api")
                .id("100")
                .type("people")
                .paging(true)
                .build();

        softly.assertThat(query.toURLString()).isEqualTo("http://example.com/api/people/100");
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

        softly.assertThat(query.toURLString()).isEqualTo("http://uninitialised.example.com/people/100?paging=false&fields=children[age],parent");

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

        softly.assertThat(missingUrl.toURLString()).isEqualTo("http://uninitialised.example.com/people/100?paging=false&fields=children[age],parent");

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

        softly.assertThat(missingType.toURLString()).isEqualTo("http://example.com/api/dataSets/100?paging=false&fields=children[age],parent");

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

        softly.assertThat(missingId.toURLString()).isEqualTo("http://example.com/api/people?paging=false&fields=children[age],parent");

        DHISQuery missingFields = DHISQuery.builder()
                .url("http://example.com/api")
                .id("100")
                .type("people")
                .build();
        softly.assertThat(missingFields.toURLString()).isEqualTo("http://example.com/api/people/100?paging=false");
    }

    @Test
    public void TestDHISQueryBuilderFilters() {
        // single filter
        DHISQuery query = DHISQuery.builder()
                .url("http://example.com/api")
                .id("100")
                .type("people")
                .beginFilter()
                .lhs("code")
                .op("eq")
                .value("001")
                .end()
                .build();

        softly.assertThat(query.toURLString()).isEqualTo("http://example.com/api/people/100?paging=false&filter=code:eq:001");

        // multiple filters
        DHISQuery multipleFilters = DHISQuery.builder()
                .url("http://example.com/api")
                .id("100")
                .type("people")
                .beginFilter()
                .lhs("code")
                .op("eq")
                .value("001")
                .end()
                .beginFilter()
                .lhs("name")
                .op("neq")
                .value("myname")
                .end()
                .build();

        softly.assertThat(multipleFilters.toURLString()).isEqualTo("http://example.com/api/people/100?paging=false&filter=code:eq:001&filter=name:neq:myname");

        // single filter no code
        DHISQuery noCode = DHISQuery.builder()
                .url("http://example.com/api")
                .id("100")
                .type("people")
                .beginFilter()
                // .lhs("code")
                .op("eq")
                .value("001")
                .end()
                .build();

        softly.assertThat(noCode.toURLString()).isEqualTo("http://example.com/api/people/100?paging=false&filter=id:eq:001");

        // single filter no op
        DHISQuery noOp = DHISQuery.builder()
                .url("http://example.com/api")
                .id("100")
                .type("people")
                .beginFilter()
                .lhs("code")
                // .op("eq")
                .value("001")
                .end()
                .build();

        softly.assertThat(noOp.toURLString()).isEqualTo("http://example.com/api/people/100?paging=false&filter=code:eq:001");

        // single filter no value
        DHISQuery noValue = DHISQuery.builder()
                .url("http://example.com/api")
                .id("100")
                .type("people")
                .beginFilter()
                .lhs("code")
                .op("eq")
                // .value("001")
                .end()
                .build();

        softly.assertThat(noValue.toURLString()).isEqualTo("http://example.com/api/people/100?paging=false&filter=code:eq");
    }

    @Test
    public void TestDHISQueryBuilderFieldAndFilters() {
        // single filter
        DHISQuery query = DHISQuery.builder()
                .url("http://example.com/api")
                .id("100")
                .type("people")
                .beginField()
                .name("children")
                .end()
                .beginFilter()
                .lhs("code")
                .op("eq")
                .value("001")
                .end()
                .build();

        softly.assertThat(query.toURLString()).isEqualTo("http://example.com/api/people/100?paging=false&filter=code:eq:001&fields=children");
    }

}
