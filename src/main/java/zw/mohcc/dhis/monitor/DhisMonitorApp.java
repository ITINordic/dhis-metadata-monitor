/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.monitor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import spark.Request;
import spark.Response;
import static spark.Spark.get;
import zw.mohcc.dhis.apiclient.DHISQuery;
import zw.mohcc.dhis.apiclient.HttpClient;
import zw.mohcc.dhis.apiclient.HttpClientFactory;

/**
 *
 * @author cliffordc
 */
public class DhisMonitorApp {

    private final DHISQuery defaultQuery;
    private final MonitorConfig config;
    private final Configuration jsonPathConf;

    public DhisMonitorApp(MonitorConfig config) {
        final DHISQuery.DHISQueryBuilder builder = dhisQueryBuilder()
                .url(config.getApiRootUrl())
                .username(config.getUsername())
                .password(config.getPassword());
        final HttpClientFactory clientFactory = config.getClientFactory();
        if (clientFactory != null) {
            builder.clientFactory(clientFactory);
        }
        jsonPathConf = Configuration.defaultConfiguration();
        defaultQuery = builder
                .build();

        this.config = config;
    }

    private DHISQuery.DHISQueryBuilder dhisQueryBuilder() {
        if (defaultQuery == null) {
            return DHISQuery.builder();
        } else {
            return defaultQuery.toBuilder();
        }
    }

    public void start() {
        get("/monitor", (Request req, Response res) -> {
            res.body(monitor());
            return res;
        });

    }

    String monitor() {
        StringBuilder sb = new StringBuilder();
        final Path repo = config.getAppHome().resolve("repo");
        Map<String, DataSetGroupConfig> notifyGroupMap = new HashMap<>();
        Map<String, Set<String>> notifyMap = new HashMap<>();
        Set<String> processed = new HashSet<>();
        for (DataSetGroupConfig dataSetGroupConfig : config.getDataSetGroups()) {
            final String dataSetGroupId = dhisUniqueObjectId(dataSetGroupConfig.getName(), "emailGroup");
            notifyGroupMap.put(dataSetGroupId, dataSetGroupConfig);
            for (String code : dataSetGroupConfig.getDataSets()) {
                populateDataSets(code, dataSetGroupId, notifyMap, processed, repo);
            }
        }
        return sb.toString();
    }

    // QUERY="fields=name,id,code,periodType,categoryCombo\[categories\[name,code,\[categoryOptions\]\]\],\
    // organisationUnits\[name,id,code\],\
    // dataElements\[name,id,code,categoryCombo\[categories\[name,code,\[categoryOptions\]\]\]\]"
    private void populateDataSets(String code, String parentObjectId, Map<String, Set<String>> notifyMap, Set<String> processed, final Path repo) {
        // poulate datasets to process
        final String depObjectId = dhisUniqueObjectId(code, "dataSets");
        addDepNotifyRequest(notifyMap, depObjectId, parentObjectId);
        if (processed.add(depObjectId)) {
            DHISQuery query = dhisQueryBuilder()
                    .type("DataSets")
                    .beginField()
                    .name("organisationUnits")
                    .beginField().name("name").end()
                    .beginField().name("id").end()
                    .beginField().name("code").end()
                    .end()
                    .beginFilter()
                    .lhs("code")
                    .op("eq")
                    .value(code)
                    .end()
                    .build();
            HttpClient httpClient = query.toHttpClient();
            String result = httpClient.call();
            try {
                writeFile(depObjectId, repo, result);
            } catch (IOException ex) {
                Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.SEVERE, null, ex);
            }
            // parser dataset and populate toprocess with dependancies
            try {
                DocumentContext ctx = JsonPath.using(jsonPathConf).parse(result);
                // organisationUnits
                List<String> orgUnits = ctx.read("$.dataSets[*].organisationUnits[*].code");
                for (String nodecode : orgUnits) {
                    processOrgUnits(nodecode, depObjectId, notifyMap, processed, repo);
                }
                // dataElements
                List<String> dataElements = ctx.read("$.dataSets[*].dataElements");
                for (String nodecode : dataElements) {
                    processDataElements(nodecode, depObjectId, notifyMap, processed, repo);
                }
                // categories
                List<String> categories = ctx.read("$.dataSets[*].categoryCombo.categories[*].code");
                for (String nodecode : categories) {
                    processCategories(nodecode, depObjectId, notifyMap, processed, repo);
                }
            } catch (IOException ex) {
                Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static String dhisUniqueObjectId(String code, String type) {
        return String.format("%s-%s", code, type);
    }

    private void processOrgUnits(String code, String parentObjectId, Map<String, Set<String>> notifyMap, Set<String> processed, final Path repo) throws IOException {
        final String depObjectId = dhisUniqueObjectId(code, "organisationUnits");
        addDepNotifyRequest(notifyMap, depObjectId, parentObjectId);
        if (processed.add(depObjectId)) {
            DHISQuery query = dhisQueryBuilder()
                    .type("organisationUnits")
                    .beginField().name("name").end()
                    .beginField().name("id").end()
                    .beginField().name("code").end()
                    .beginFilter()
                    .lhs("code")
                    .op("eq")
                    .value(code)
                    .end()
                    .build();
            HttpClient httpClient = query.toHttpClient();
            String result = httpClient.call();
            writeFile(depObjectId, repo, result);
        }
    }

    private void processCategories(String code, String parentObjectId, Map<String, Set<String>> notifyMap, Set<String> processed, final Path repo) throws IOException {
        final String depObjectId = dhisUniqueObjectId(code, "categories");
        addDepNotifyRequest(notifyMap, depObjectId, parentObjectId);
        if (processed.add(depObjectId)) {
            DHISQuery query = dhisQueryBuilder()
                    .type("categories")
                    .beginField().name("name").end()
                    .beginField().name("id").end()
                    .beginField().name("code").end()
                    .beginFilter()
                    .lhs("code")
                    .op("eq")
                    .value(code)
                    .end()
                    .build();
            HttpClient httpClient = query.toHttpClient();
            String result = httpClient.call();
            writeFile(depObjectId, repo, result);
        }
    }

    private void processDataElements(String code, String parentObjectId, Map<String, Set<String>> notifyMap, Set<String> processed, final Path repo) throws IOException {
        final String depObjectId = dhisUniqueObjectId(code, "dataElements");
        addDepNotifyRequest(notifyMap, depObjectId, parentObjectId);
        if (processed.add(depObjectId)) {
            DHISQuery query = dhisQueryBuilder()
                    .type("dataElements")
                    .beginField().name("name").end()
                    .beginField().name("id").end()
                    .beginField().name("code").end()
                    .beginField().name("categories") // begin cat
                    .beginField().name("name").end()
                    .beginField().name("code").end()
                    .end()
                    .beginFilter()
                    .lhs("code")
                    .op("eq")
                    .value(code)
                    .end()
                    .build();
            HttpClient httpClient = query.toHttpClient();
            String result = httpClient.call();
            writeFile(depObjectId, repo, result);
            // parse json
            DocumentContext ctx = JsonPath.using(jsonPathConf).parse(result);
            // categories
            List<String> categories = ctx.read("$.dataSets[*].categoryCombo.categories[*].code");
            for (String nodecode : categories) {
                processCategories(nodecode, depObjectId, notifyMap, processed, repo);
            }
        }
    }

    private void writeFile(String filename, final Path repo, String result) throws IOException {
        FileUtils.writeStringToFile(repo.resolve(filename).toFile(), result);
    }

    private void addDepNotifyRequest(Map<String, Set<String>> notifyMap, final String depObjectId, String parentObjectId) {
        if (!notifyMap.containsKey(depObjectId)) {
            notifyMap.put(depObjectId, new HashSet<>());
        }
        Set<String> depNotifySet = notifyMap.get(depObjectId);
        depNotifySet.add(parentObjectId);
    }
}
