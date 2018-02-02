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
package zw.org.mohcc.dhis.monitor;

import java.util.Set;
import java.util.function.Function;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 *
 * @author cliffordc
 */

@Builder
@Value
public class DataSetGroupConfig {
    private String name;
    private String email;
    @Singular
    private Set<String> dataSets;
    
    
    public static DataSetGroupConfig.DataSetGroupConfigBuilder<DataSetGroupConfig> builder() {
        return new DataSetGroupConfig.DataSetGroupConfigBuilder<>();
    }

    public static class DataSetGroupConfigBuilder<T> {

        private Function<DataSetGroupConfig, T> callback = dsc -> (T) this;

        public static <T> DataSetGroupConfigBuilder<T> begin(Function<DataSetGroupConfig, T> callback) {
            DataSetGroupConfigBuilder<T> builder = new DataSetGroupConfigBuilder<>();
            builder.callback = callback;
            return builder;
        }

        public T end() {
            return callback.apply(build());
        }
    }
    
}
