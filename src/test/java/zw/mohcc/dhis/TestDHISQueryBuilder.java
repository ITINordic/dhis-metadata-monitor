/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author cliffordc
 */
public class TestDHISQueryBuilder {
    
    @Test
    public void TestDHISQueryBuilderToURLString(){

        DHISQuery query = DHISQueryBuilder.builder()
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
        
        assertThat(query.toURLString()).isEqualTo("http://example.com/api/people/100?fields=children[age],parent");
    }
}
