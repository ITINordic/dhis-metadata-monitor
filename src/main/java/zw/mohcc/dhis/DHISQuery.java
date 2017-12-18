/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis;

import java.util.List;
import lombok.Builder;
import lombok.Singular;

/**
 *
 * @author cliffordc
 */

@Builder
public class DHISQuery {
    String url;
    String type;
    String id;
    String accept;
    @Singular
    List<Field> fields;
}
