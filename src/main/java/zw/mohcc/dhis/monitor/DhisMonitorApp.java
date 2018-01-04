/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.monitor;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import spark.Request;
import spark.Response;
import static spark.Spark.get;
import zw.mohcc.dhis.apiclient.DHISQuery;
import zw.mohcc.dhis.apiclient.HttpClient;
import zw.mohcc.dhis.apiclient.HttpClientFactory;
import zw.mohcc.dhis.email.EmailClient;
import zw.mohcc.dhis.email.FileEmailClient;
import zw.mohcc.dhis.gitclient.GitClient;
import zw.mohcc.dhis.gitclient.GitProcessingFailedException;

/**
 *
 * @author cliffordc
 */
public class DhisMonitorApp {

    private final DHISQuery defaultQuery;
    private final MonitorConfig config;
    private final Configuration jsonPathConf;
    private final EmailClient emailClient;

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

        if(config.getEmailClient() == null){
            emailClient = new FileEmailClient(config.getAppHome().resolve("email"));
        } else {
            emailClient = config.getEmailClient();
        }
        
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
            res.body(monitor().stream().map(notify -> notify.toString()).collect(Collectors.joining("\n")));
            return res;
        });

    }

    Set<Notify> monitor() {
        try {
            // Nothing to process
            if(config.getDataSetGroups().isEmpty()) return Collections.emptySet();
            
            final Path repo = config.getAppHome().resolve("repo");
            Map<String, Notify> notifyGroupMap = new HashMap<>();
            Map<String, Set<String>> notifyMap = new HashMap<>();
            Set<String> processed = new HashSet<>();
            for (DataSetGroupConfig dataSetGroupConfig : config.getDataSetGroups()) {
                final String dataSetGroupId = dhisUniqueObjectId(dataSetGroupConfig.getName(), "emailGroup");
                notifyGroupMap.put(dataSetGroupId, new Notify(dataSetGroupConfig));
                for (String code : dataSetGroupConfig.getDataSets()) {
                    populateDataSets(code, dataSetGroupId, notifyMap, processed, repo);
                }
            }
            GitClient gitClient = new GitClient();
            String diffString = gitClient.process(repo);
            Scanner scanner = new Scanner(diffString);

            Map<String, String> diffs = new HashMap<>();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                pw.println(line);
                if (line.startsWith("diff --git")) {
                    diffs.put(line.substring(line.lastIndexOf("/") + 1), sw.toString());
                    pw.close();
                    sw = new StringWriter();
                    pw = new PrintWriter(sw);
                }
            }
            scanner.close();
            // initialise notification
            Map<String, Set<String>> toNotifyWithMsgs = new HashMap<>();
            for (Map.Entry<String, String> diff : diffs.entrySet()) {
                final String filename = diff.getKey();
                final String key = filename.substring(0, filename.length() - 5);
                String diffMsg = diff.getValue();
                if (!toNotifyWithMsgs.containsKey(key)) {
                    toNotifyWithMsgs.put(key, new HashSet<>());
                }
                Set<String> msgs = toNotifyWithMsgs.get(key);
                msgs.add(diffMsg);
            }
            // push notification
            while (true) {
                Optional<String> item
                        = toNotifyWithMsgs.keySet().stream().findFirst();
                if (item.isPresent()) {
                    String mkey = item.get();
                    Set<String> msgs = toNotifyWithMsgs.remove(mkey);
                    if (notifyMap.containsKey(mkey)) {
                        // add msgs to parent
                        Set<String> parents = notifyMap.get(mkey);
                        for (String key : parents) {
                            if (!toNotifyWithMsgs.containsKey(key)) {
                                toNotifyWithMsgs.put(key, msgs);
                            } else {
                                Set<String> existingMsgs = toNotifyWithMsgs.get(key);
                                existingMsgs.addAll(msgs);
                            }
                        }
                    } else {
                        Notify notify = notifyGroupMap.get(mkey);
                        notify.getMessages().addAll(msgs);    
                    }

                } else {
                    break;
                }
            }
            
            // remove groups without messages
            final Set<Notify> notifySet 
                    = notifyGroupMap.values().stream().filter(n -> !n.getMessages().isEmpty()).collect(Collectors.toSet());
            
            // send emails
            for (Notify value : notifySet) {
                final String email = value.getGroup().getEmail();
                emailClient.sendEmail(email, value.getMessages().stream().collect(Collectors.joining("\n\n\n")));
            }

            return notifySet;
        } catch (GitProcessingFailedException ex) {
            Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Collections.emptySet();
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
                    .type("dataSets")
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

    static String dhisUniqueObjectId(String code, String type) {
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
        FileUtils.writeStringToFile(repo.resolve(filename + ".json").toFile(), result);
    }

    private void addDepNotifyRequest(Map<String, Set<String>> notifyMap, final String depObjectId, String parentObjectId) {
        if (!notifyMap.containsKey(depObjectId)) {
            notifyMap.put(depObjectId, new HashSet<>());
        }
        Set<String> depNotifySet = notifyMap.get(depObjectId);
        depNotifySet.add(parentObjectId);
    }
}
