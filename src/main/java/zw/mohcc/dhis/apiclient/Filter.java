/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis;

import java.util.List;
import java.util.function.Function;
import lombok.Builder;
import lombok.Singular;

/**
 *
 * @author cliffordc
 */
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
        } else if (op.equalsIgnoreCase("in")) {
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
