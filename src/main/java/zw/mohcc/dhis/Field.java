/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Singular;

/**
 *
 * @author cliffordc
 */
@Builder
public class Field {

    private String name;
    @Singular
    private List<Field> fields;

    StringBuilder renderField(StringBuilder build) {
        build.append(name);
        if(fields.isEmpty()) {
            return build;
        }
        build.append("[");
        renderFields(build, fields);
        return build.append("]");
    }

    public static StringBuilder renderFields(StringBuilder build, List<Field> fields) {
        if (fields != null) {
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                if(i != 0) build.append(",");
                field.renderField(build);
            }
        }
        return build;
    }
}
