/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.monitor;

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
