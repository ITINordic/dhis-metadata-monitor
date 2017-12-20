/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Singular;

/**
 *
 * @author cliffordc
 */
@Builder(toBuilder = true)
public class DHISQuery {

    private String url;
    private String type;
    private String id;
    private String accept;
    @Singular
    private List<Field> fields;
    @Singular
    private List<Filter> filters;

    public String toURLString() {
        StringBuilder build = new StringBuilder();
        build = renderDHISQuery(build);
        return build.toString();
    }

    protected StringBuilder renderDHISQuery(StringBuilder urlBuild) {
        urlBuild = renderURL(urlBuild);
        urlBuild = renderURLQuery(urlBuild);
        return urlBuild;
    }

    private boolean querySeperator(boolean isFirst, StringBuilder queryBuild) {
        queryBuild.append(isFirst ? "?" : "&");
        return false;
    }

    private StringBuilder renderURLQuery(StringBuilder queryBuild) {
        Boolean isFirst = true;
        for (Filter filter : filters) {
            querySeperator(isFirst, queryBuild);
            queryBuild.append("filter=");
            filter.renderFilter(queryBuild);
        }

        if (fields.size() > 0) {
            querySeperator(isFirst, queryBuild);
            queryBuild.append("fields=");
            Field.renderFields(queryBuild, fields);
        }
        return queryBuild;
    }

    private StringBuilder renderURL(StringBuilder build) {
        build.append(url == null ? "http://uninitialised.example.com" : url)
                .append('/')
                .append(type == null ? "dataSets" : type);
        if (id != null) {
            build.append("/")
                    .append(id);
        }
        return build;
    }

    public static DHISQueryBuilder builder() {
        if (defaultQuery == null) {
            return new DHISQueryBuilder();
        } else {
            return defaultQuery.toBuilder();
        }

    }

    private static DHISQuery defaultQuery;

    public static void setDefault(DHISQuery query) {
        defaultQuery = query;

    }

    public static class DHISQueryBuilder {

        public Field.FieldBuilder<DHISQuery.DHISQueryBuilder> beginField() {
            return Field.FieldBuilder.begin(f -> (DHISQuery.DHISQueryBuilder) this.field(f));
        }

        public Filter.FilterBuilder<DHISQuery.DHISQueryBuilder> beginFilter() {
            return Filter.FilterBuilder.begin(f -> (DHISQuery.DHISQueryBuilder) this.filter(f));
        }
    }
}
