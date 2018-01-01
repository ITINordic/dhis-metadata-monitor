/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.monitor;

import org.apache.commons.io.FileUtils;
import spark.Request;
import spark.Response;
import static spark.Spark.get;
import zw.mohcc.dhis.apiclient.DHISQuery;
import zw.mohcc.dhis.apiclient.HttpClient;

/**
 *
 * @author cliffordc
 */
public class DhisMonitorApp {

    private final DHISQuery defaultQuery;
    private final MonitorConfig config;

    public DhisMonitorApp(MonitorConfig config) {
        defaultQuery = dhisQueryBuilder()
                .url(config.getApiRootUrl())
                .username(config.getUsername())
                .password(config.getPassword())
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
        StringBuilder sb = new StringBuilder();
        get("/monitor", (Request req, Response res) -> {
            for (DataSetGroupConfig dataSet : config.getDataSetGroups()) {
                final String filename = String.format("{0}.json", dataSet.getName());
                DHISQuery query = dhisQueryBuilder().build();
                HttpClient httpClient = query.toHttpClient();
                String result = httpClient.call();
                sb.append(result);
                FileUtils.writeStringToFile(config.getUserHome().resolve(filename).toFile(), result);
            }
            res.body(sb.toString());
            return res;
        });

    }
}
