/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.org.mohcc.dhis.apiclient;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

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
    private String username;
    private String password;
    @Builder.Default
    private boolean paging = false;
    @Singular
    private List<Field> fields;
    @Singular
    private List<Filter> filters;

    @Builder.Default
    private HttpClientFactory clientFactory = new OkHttpClientFactory();

    @Singular
    private Map<String, String> headers;

    public HttpClient toHttpClient() {
        final HttpClientFactory hcf;
        hcf = clientFactory == null ? new OkHttpClientFactory() : clientFactory;
        return hcf.getInstance(username, password, headers, toURLString());
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
        boolean isFirst = true;
        if (paging == false) {
            isFirst = querySeperator(isFirst, queryBuild);
            queryBuild.append("paging=false");
        }
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

    public static class DHISQueryBuilder {

        public Field.FieldBuilder<DHISQuery.DHISQueryBuilder> beginField() {
            return Field.FieldBuilder.begin(f -> (DHISQuery.DHISQueryBuilder) this.field(f));
        }

        public Filter.FilterBuilder<DHISQuery.DHISQueryBuilder> beginFilter() {
            return Filter.FilterBuilder.begin(f -> (DHISQuery.DHISQueryBuilder) this.filter(f));
        }

        public DHISQueryBuilder accept(String accept) {
            return this.header("Accept", accept);
        }
    }
}
