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
import java.util.function.Function;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 *
 * @author cliffordc
 */
@Value
@Builder
public class Filter {

    String lhs;
    String op;
    @Singular
    List<String> values;

    StringBuilder renderFilter(StringBuilder build) {
        build.append(lhs == null ? "id" : lhs)
                .append(":")
                .append(op == null ? "eq" : op);
        if (values.isEmpty()) {
            // Do Nothing
        } else if (op != null && op.equalsIgnoreCase("in")) {
            build.append(":[");
            for (int i = 0; i < values.size(); i++) {
                if (i != 0) {
                    build.append(",");
                }
                build.append(values.get(i));
            }
            build.append("]");
        } else {
            build.append(":")
                    .append(values.get(0));
        }
        return build;
    }
    
    public static Filter.FilterBuilder<Filter.FilterBuilder> builder() {
        return new Filter.FilterBuilder<>();
    }
    
    public static class FilterBuilder<T> {

        private Function<Filter, T> callback;

        public T end() {
            return callback.apply(build());
        }

        static <T> Filter.FilterBuilder<T> begin(Function<Filter, T> callback) {
            Filter.FilterBuilder<T> builder = new Filter.FilterBuilder<>();
            builder.callback = callback;
            return builder;
        }
    }
}
