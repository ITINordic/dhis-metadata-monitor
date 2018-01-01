/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.apache.commons.beanutils.PropertyUtils;

/**
 *
 * @author cliffordc
 */

@Builder
@Value
public class MonitorConfig {

    private String apiRootUrl;
    private String username;
    private String password;
    private Path userHome;

    @Singular
    private Set<DataSetGroupConfig> dataSetGroups;

    public static class MonitorConfigBuilder {

        public MonitorConfigBuilder addJsonConfig(MonitorConfigBuilder builder, String json) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                MonitorConfigBuilder build = mapper.readValue(json, MonitorConfigBuilder.class);
                PropertyUtils.copyProperties(build, this);
            } catch (IOException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                Logger.getLogger(MonitorConfig.class.getName()).log(Level.SEVERE, null, ex);
            }

            return this;
        }

        private boolean updateStringFields(final String key, final Object value) {
            try {
                Field field = MonitorConfig.class.getField(key);
                if (!field.getType().equals(String.class)) {
                    return false;
                }
                PropertyUtils.setProperty(this, key, value);
            } catch (NoSuchFieldException
                    | SecurityException
                    | IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException ex) {
                Logger.getLogger(MonitorConfig.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        }

        public MonitorConfigBuilder addPropertiesConfig(Properties properties) {
            Pattern isDataSet = Pattern.compile("dataset\\.(?<name>[^\\.]+)\\.(?<prop>[^.]+)");
            Map<String, DataSetGroupConfig.DataSetGroupConfigBuilder<DataSetGroupConfig>> dataSetMap;
            dataSetMap = new HashMap<>();
            for (Map.Entry<Object, Object> en : properties.entrySet()) {

                final String key = (String) en.getKey();
                final String value = (String) en.getValue();
                Matcher match = isDataSet.matcher(key);
                final String name = match.group("name");
                final String prop = match.group("prop");

                if (match.matches()) {
                    DataSetGroupConfig.DataSetGroupConfigBuilder<DataSetGroupConfig> buildDataSetGroup;

                    if (!dataSetMap.containsKey(name)) {
                        buildDataSetGroup = DataSetGroupConfig.builder();
                        buildDataSetGroup.name(name);
                        dataSetMap.put(name, buildDataSetGroup);
                    }
                    buildDataSetGroup = dataSetMap.get(name);
                    switch (prop) {
                        case "email":
                            buildDataSetGroup.email(value);
                            break;
                        case "list":
                            for (String dname : value.split(",")) {
                                buildDataSetGroup.dataSet(dname);
                            }
                    }
                } else {
                    updateStringFields(key, value);
                }

            }
            
            dataSetMap.values().forEach((buildDataSetGroup) -> {
                this.dataSetGroup(buildDataSetGroup.build());
            });

            return this;
        }

        public DataSetGroupConfig.DataSetGroupConfigBuilder beginDataSet() {
            return DataSetGroupConfig.DataSetGroupConfigBuilder.begin((DataSetGroupConfig dsgc) -> {
                return this.dataSetGroup(dsgc);
            });
        }
    }

}
