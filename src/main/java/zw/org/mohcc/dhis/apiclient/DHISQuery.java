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
