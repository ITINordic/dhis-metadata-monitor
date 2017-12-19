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
public class DHISQuery {

    private String url;
    private String type;
    private String id;
    private String accept;
    @Singular
    private List<Field> fields;

    public String toURLString() {
        StringBuilder build = new StringBuilder();
        build = renderDHISQuery(build);
        return build.toString();
    }

    protected StringBuilder renderDHISQuery(StringBuilder urlBuild) {
        urlBuild = renderURL(urlBuild);
        StringBuilder queryBuild = renderURLQuery();
        if (queryBuild.length() != 0) {
            urlBuild.append("?")
                    .append(queryBuild);
        }
        return urlBuild;
    }

    private StringBuilder renderURLQuery() {
        StringBuilder queryBuild = new StringBuilder();

        queryBuild.append("fields=");
        Field.renderFields(queryBuild, fields);
        return queryBuild;
    }

    private StringBuilder renderURL(StringBuilder build) {
        return build.append(url)
                .append('/')
                .append(type)
                .append('/')
                .append(id);
    }
}
