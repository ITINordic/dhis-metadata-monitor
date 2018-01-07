/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.org.mohcc.dhis.monitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import zw.org.mohcc.dhis.apiclient.HttpClientFactory;
import zw.org.mohcc.dhis.email.EmailClient;

/**
 *
 * @author cliffordc
 */
@Builder(toBuilder = true)
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitorConfig {

    private String apiRootUrl;
    private String username;
    private String password;
    private String javaLoggingFile;

    @Builder.Default
    private String bindAddress = "127.0.0.1";

    private Path appHome;
    private HttpClientFactory clientFactory;
    private EmailClient emailClient;

    @Singular
    private Map<String, String> mailSettings;

    @Builder.Default
    private Configuration jsonPathsConfig = Configuration.defaultConfiguration();

    @Singular
    private Set<DataSetGroupConfig> dataSetGroups;

    public String getDefaultFromEmailAccount() {
        final String email = mailSettings.get("mail.default.email");
        return email == null ? "info@example.org" : email;
    }

    public static class MonitorConfigBuilder {

        public MonitorConfigBuilder addJsonConfig(String json) {

            ObjectMapper mapper = new ObjectMapper();
            try {
                MonitorConfig config = mapper.readValue(json, MonitorConfig.class);

                for (Field field : MonitorConfig.class.getDeclaredFields()) {
                    final String name = field.getName();
                    updateFields(name, PropertyUtils.getProperty(config, name));
                }

                JsonNode jn = mapper.readTree(json);
                JsonNode mail = jn.get("mail");
                processMailSettings(mail, "mail");

            } catch (IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException
                    | NoSuchFieldException
                    | IOException ex) {
                Logger.getLogger(MonitorConfig.class.getName()).log(Level.SEVERE, null, ex);
            }

            return this;
        }

        private void processMailSettings(JsonNode mail, String prefix) {
            for (Iterator<Map.Entry<String, JsonNode>> iterator = mail.fields(); iterator.hasNext();) {
                Map.Entry<String, JsonNode> field = iterator.next();
                final String newPrefix = String.format("%s.%s", prefix, field.getKey());
                if (field.getValue().isObject()) {
                    processMailSettings(field.getValue(), newPrefix);
                } else {
                    mailSetting(newPrefix, field.getValue().asText());
                }

            }
        }

        private boolean updateStringFields(final String key, final Object value) {
            try {
                Field field = MonitorConfig.class.getDeclaredField(key);
                if (!field.getType().equals(String.class)) {
                    return false;
                }
                updateFields(key, value);
                return true;
            } catch (NoSuchFieldException
                    | SecurityException
                    | IllegalAccessException ex) {
                Logger.getLogger(MonitorConfig.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        private void updateFields(final String key, final Object value) throws IllegalAccessException, NoSuchFieldException {
            // FIXME: work around different types used between MonitorConfig and MonitorConfigBuilder
            if (value == null) {
                return;
            } else if (value instanceof Map) {
                Map<Object, Object> map = (Map<Object, Object>) value;
                final ArrayList keyArrayList = new ArrayList();
                final ArrayList valueArrayList = new ArrayList();
                for (Map.Entry<Object, Object> en : map.entrySet()) {
                    keyArrayList.add(en.getKey());
                    valueArrayList.add(en.getValue());
                }
                Field builderFieldKey = MonitorConfigBuilder.class.getDeclaredField(key + "$key");
                Field builderFieldValue = MonitorConfigBuilder.class.getDeclaredField(key + "$value");
                builderFieldKey.set(this, keyArrayList);
                builderFieldValue.set(this, valueArrayList);
            } else if (value instanceof Set) {
                Field builderField = MonitorConfigBuilder.class.getDeclaredField(key);
                builderField.set(this, new ArrayList((Set) value));
            } else {
                Field builderField = MonitorConfigBuilder.class.getDeclaredField(key);
                builderField.set(this, value);
            }
        }

        public MonitorConfigBuilder addJsonPathJacksonConfiguration() {

            jsonPathsConfig(Configuration.builder()
                    .jsonProvider(new JacksonJsonProvider())
                    .mappingProvider(new JacksonMappingProvider())
                    .build());
            return this;
        }

        public MonitorConfigBuilder addPropertiesConfig(Properties properties) {
            Pattern isDataSet = Pattern.compile("dataset\\.(?<name>[^\\.]+)\\.(?<prop>[^.]+)");
            Map<String, DataSetGroupConfig.DataSetGroupConfigBuilder<DataSetGroupConfig>> dataSetMap;
            dataSetMap = new HashMap<>();
            for (Map.Entry<Object, Object> en : properties.entrySet()) {
                final String key = (String) en.getKey();
                final String value = (String) en.getValue();
                if (key.startsWith("mail.")) {
                    mailSetting(key, value);
                    continue;
                }
                Matcher match = isDataSet.matcher(key);
                if (match.matches()) {
                    final String name = match.group("name");
                    final String prop = match.group("prop");
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

        public DataSetGroupConfig.DataSetGroupConfigBuilder<MonitorConfigBuilder> beginDataSet() {
            return DataSetGroupConfig.DataSetGroupConfigBuilder.begin((DataSetGroupConfig dsgc) -> {
                return this.dataSetGroup(dsgc);
            });
        }
    }

}
