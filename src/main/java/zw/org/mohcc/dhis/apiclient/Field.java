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
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 *
 * @author cliffordc
 */

@Value
@Builder
public class Field {

    private String name;
    @Singular
    private List<Field> fields;

    StringBuilder renderField(StringBuilder build) {
        build.append(name);
        if (fields.isEmpty()) {
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
                if (i != 0) {
                    build.append(",");
                }
                field.renderField(build);
            }
        }
        return build;
    }

    public static Field.FieldBuilder<Field.FieldBuilder> builder() {
        return new Field.FieldBuilder<>();
    }

    public static class FieldBuilder<T> {

        private Function<Field, T> callback;

        public T end() {
            return callback.apply(build());
        }

        static <T> Field.FieldBuilder<T> begin(Function<Field, T> callback) {
            Field.FieldBuilder<T> builder = new Field.FieldBuilder<>();
            builder.callback = callback;
            return builder;
        }

        public Field.FieldBuilder<Field.FieldBuilder<T>> beginField() {
            return Field.FieldBuilder.begin(f -> (Field.FieldBuilder<T>) this.field(f));
        }
    }
}
