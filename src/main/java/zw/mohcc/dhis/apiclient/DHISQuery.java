/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.apiclient;

import java.util.Base64;
import java.util.List;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import okhttp3.Request;

/**
 *
 * @author cliffordc
 */

@Value
@Builder(toBuilder = true)
public class DHISQuery {

    private String url;
    private String type;
    private String id;
    private String accept;
    private String username;
    private String password;
    @Singular
    private List<Field> fields;
    @Singular
    private List<Filter> filters;

    public Request toHttpClient() {
        Request request = new Request.Builder()
                .addHeader(
                        "Authorization",
                        getBasicAuthorization(username,
                                password
                        )
                ).addHeader("Accept", accept)
                .url(toURLString())
                .build();
        return request;
    }

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
            isFirst = querySeperator(isFirst, queryBuild);
            queryBuild.append("filter=");
            filter.renderFilter(queryBuild);
        }

        if (fields.size() > 0) {
            isFirst = querySeperator(isFirst, queryBuild);
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

    public static String getBasicAuthorization(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
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
