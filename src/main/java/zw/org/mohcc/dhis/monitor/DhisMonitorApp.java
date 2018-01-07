/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.org.mohcc.dhis.monitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Delta;
import com.github.difflib.patch.Patch;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.escaper.EscapingStrategy;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.sun.prism.es2.ES2Graphics;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
import zw.org.mohcc.dhis.apiclient.DHISQuery;
import zw.org.mohcc.dhis.apiclient.HttpClient;
import zw.org.mohcc.dhis.apiclient.HttpClientFactory;
import zw.org.mohcc.dhis.email.EmailClient;
import zw.org.mohcc.dhis.email.FileEmailClient;
import zw.org.mohcc.dhis.gitclient.GitClient;
import zw.org.mohcc.dhis.gitclient.GitProcessingFailedException;

/**
 *
 * @author cliffordc
 */
public class DhisMonitorApp {

    private final DHISQuery defaultQuery;
    private final MonitorConfig config;
    private final Configuration jsonPathConf;
    private final EmailClient emailClient;
    private PebbleTemplate emailTemplate;
    private PebbleTemplate diffTableTemplate;
    private PebbleTemplate diffFilesTemplate;

    public DhisMonitorApp(MonitorConfig config) {
        final DHISQuery.DHISQueryBuilder builder = dhisQueryBuilder()
                .url(config.getApiRootUrl())
                .username(config.getUsername())
                .password(config.getPassword());
        final HttpClientFactory clientFactory = config.getClientFactory();
        if (clientFactory != null) {
            builder.clientFactory(clientFactory);
        }
        defaultQuery = builder
                .build();

        jsonPathConf = config.getJsonPathsConfig();

        if (config.getEmailClient() == null) {
            emailClient = new FileEmailClient(config.getAppHome().resolve("email"));
        } else {
            emailClient = config.getEmailClient();
        }

        this.config = config;

        try {
            PebbleEngine engine = new PebbleEngine.Builder().addEscapingStrategy("space", new EscapingStrategy() {
                @Override
                public String escape(String input) {
                    return input.replace(" ", "&nbsp;");
                }
            }).build();
            emailTemplate = engine.getTemplate("email.ptl");
//            diffTableTemplate = engine.getTemplate("diffTable.ptl");
//            diffFilesTemplate = engine.getTemplate("diffFiles.ptl");
        } catch (PebbleException ex) {
            Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.INFO, "Begin processing...");
            // Nothing to process
            if (config.getDataSetGroups().isEmpty()) {
                return Collections.emptySet();
            }

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

            Map<String, Map<String, Object>> fileDiffs = new HashMap<>();
            List<String> toDiff = new LinkedList<>();
            DiffRowGenerator generator = DiffRowGenerator.create()
                    .showInlineDiffs(true)
                    .inlineDiffByWord(true)
                    .oldTag(f -> f ? "<del>" : "</del>")
                    //introduce markdown style for strikethrough
                    .newTag(f -> f ? "<b>" : "</b>") //introduce markdown style for bold
                    .build();

            String line = "";
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (line.startsWith("diff --git")) {
                    break;
                }
            }
            while (scanner.hasNextLine()) {
                final String filename = line.substring(line.lastIndexOf("/") + 1);
                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    if (line.startsWith("diff --git")) {
                        break;
                    }
                    toDiff.add(line);
                }
                Patch<String> patch = UnifiedDiffUtils.parseUnifiedDiff(toDiff);
                toDiff = new LinkedList<>();
                List<Object> patchList = new LinkedList<>();
                for (Delta<String> delta : patch.getDeltas()) {
                    List<DiffRow> diffRows = generator.generateDiffRows(
                            delta.getOriginal().getLines(),
                            delta.getRevised().getLines()
                    );
                    Map<String, Object> deltaCtx = new HashMap<>();
                    deltaCtx.put("delta", delta);
                    deltaCtx.put("diffRows", diffRows);
                    patchList.add(deltaCtx);
                }
                Map<String, Object> fileCtx = new HashMap<>();
                fileCtx.put("name", filename);
                fileCtx.put("patches", patchList);
                fileDiffs.put(filename, fileCtx);

            }
            scanner.close();
            // initialise notification
            Map<String, Set<Map<String, Object>>> toNotifyWithMsgs = new HashMap<>();

            for (Map.Entry<String, Map<String, Object>> diff : fileDiffs.entrySet()) {
                final String filename = diff.getKey();
                final String key = filename.substring(0, filename.length() - 5);
                Map<String, Object> fileCtx = diff.getValue();
                if (!toNotifyWithMsgs.containsKey(key)) {
                    toNotifyWithMsgs.put(key, new HashSet<>());
                }
                Set<Map<String, Object>> msgs = toNotifyWithMsgs.get(key);
                msgs.add(fileCtx);
            }
            // push notification
            while (true) {
                Optional<String> item
                        = toNotifyWithMsgs.keySet().stream().findFirst();
                if (item.isPresent()) {
                    String mkey = item.get();
                    Set<Map<String, Object>> msgs = toNotifyWithMsgs.remove(mkey);
                    if (notifyMap.containsKey(mkey)) {
                        // add msgs to parent
                        Set<String> parents = notifyMap.get(mkey);
                        for (String key : parents) {
                            if (!toNotifyWithMsgs.containsKey(key)) {
                                toNotifyWithMsgs.put(key, msgs);
                            } else {
                                Set<Map<String, Object>> existingMsgs = toNotifyWithMsgs.get(key);
                                existingMsgs.addAll(msgs);
                            }
                        }
                    } else {
                        Notify notify = notifyGroupMap.get(mkey);
                        notify.getFileDiffs().addAll(msgs);
                    }

                } else {
                    break;
                }
            }

            // remove groups without messages
            final Set<Notify> notifySet
                    = notifyGroupMap.values().stream().filter(n -> !n.getFileDiffs().isEmpty()).collect(Collectors.toSet());

            // send emails
            for (Notify value : notifySet) {
                final String email = value.getGroup().getEmail();

                Map<String, Object> templateCtx = new HashMap<>();
                templateCtx.put("files", value.getFileDiffs());
                try {
                    final String[] recipients = new String[]{email};
                    String msg = writeTemplate(emailTemplate, templateCtx);
                    Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.INFO,
                            String.format("Sending emails to: %s\n%s",
                                    Arrays.stream(recipients).collect(Collectors.joining(", ")),
                                    msg
                            )
                    );
                    emailClient.sendMessage(config.getDefaultFromEmailAccount(), recipients,
                            "DHIS Monitoring Report",
                            msg);
                } catch (IOException ex) {
                    Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.INFO, "Finished processing.");

            return notifySet;
        } catch (GitProcessingFailedException | DiffException ex) {
            Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Collections.emptySet();
    }
    // QUERY="fields=name,id,code,periodType,categoryCombo\[categories\[name,code,\[categoryOptions\]\]\],\
    // organisationUnits\[name,id,code\],\
    // dataElements\[name,id,code,categoryCombo\[categories\[name,code,\[categoryOptions\]\]\]\]"

    private String writeTemplate(PebbleTemplate compiledTemplate, Map<String, Object> ctx) throws IOException {
        try {
            Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, ctx);
            return writer.toString();
        } catch (PebbleException ex) {
            Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    private void populateDataSets(String code, String parentObjectId, Map<String, Set<String>> notifyMap, Set<String> processed, final Path repo) {
        Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.INFO, "Begin processing DataSets...");
        // poulate datasets to process
        final String depObjectId = dhisUniqueObjectId(code, "dataSets");
        addDepNotifyRequest(notifyMap, depObjectId, parentObjectId);
        if (processed.add(depObjectId)) {
            try {
                DHISQuery query = dhisQueryBuilder()
                        .type("dataSets")
                        .beginField() // 1
                        .name("name")
                        .end()
                        .beginField() // 1
                        .name("id")
                        .end()
                        .beginField() // 1
                        .name("code")
                        .end()
                        .beginField() // 1
                        .name("periodType")
                        .end()
                        .beginField() // 1
                        .name("categoryCombo")
                        .beginField() // 2
                        .name("categories")
                        .beginField() // 3
                        .name("name")
                        .end()
                        .beginField() // 3
                        .name("id")
                        .end()
                        .beginField() // 3
                        .name("code")
                        .end()
                        .end()
                        .end()
                        .beginField() // 1
                        .name("dataSetElements")
                        .beginField() // 2
                        .name("categoryCombo")
                        .beginField() // 3
                        .name("categories")
                        .beginField() // 4
                        .name("name")
                        .end()
                        .beginField() // 4
                        .name("id")
                        .end()
                        .beginField() // 4
                        .name("code")
                        .end()
                        .end()
                        .end()
                        .beginField() // 2
                        .name("dataElement")
                        .beginField() // 3
                        .name("name")
                        .end()
                        .beginField() // 3
                        .name("id")
                        .end()
                        .beginField() // 3
                        .name("code")
                        .end()
                        .beginField() // 3
                        .name("categoryCombo")
                        .beginField() // 4
                        .name("categories")
                        .beginField() // 5
                        .name("name")
                        .end()
                        .beginField() // 5
                        .name("id")
                        .end()
                        .beginField() // 5
                        .name("code")
                        .end()
                        .end()
                        .end()
                        .end()
                        .end()
                        .beginField() // 1
                        .name("organisationUnits")
                        .beginField() // 2
                        .name("name")
                        .end()
                        .beginField() // 2
                        .name("id")
                        .end()
                        .beginField() // 2
                        .name("code")
                        .end()
                        .end()
                        .beginFilter()
                        .lhs("code")
                        .op("eq")
                        .value(code)
                        .end()
                        .build();
                HttpClient httpClient = query.toHttpClient();
                String result = queryAndSort(httpClient);
                writeFile(depObjectId, repo, result);

                // parser dataset and populate toprocess with dependancies
                DocumentContext ctx = JsonPath.using(jsonPathConf).parse(result);
                Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.INFO, "Begin processing DataElements...");
                // dataElements
                List<String> dataElements = ctx.read("$.dataSets[*].dataSetElements[*].dataElement.code");
                for (String nodecode : dataElements) {
                    processDataElements(nodecode, depObjectId, notifyMap, processed, repo);
                }
                // categories
                Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.INFO, "Begin processing Categories...");
                // categories on dataSet.categoryCombo do not seem to be used uncomment to add them
                // List<String> dataSetCategories = ctx.read("$.dataSets[*].categoryCombo.categories[*].code");
                List<String> categories = ctx.read("$.dataSets[*].dataSetElements[*].categoryCombo.categories[*].code");

                for (String nodecode : categories) {
                    processCategories(nodecode, depObjectId, notifyMap, processed, repo);
                }
                Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.INFO, "Begin processing OrganisationUnits...");
                // organisationUnits
                List<String> orgUnits = ctx.read("$.dataSets[*].organisationUnits[*].code");
                for (Object nodecode : orgUnits) {
                    processOrgUnits((String) nodecode, depObjectId, notifyMap, processed, repo);
                }
            } catch (IOException ex) {
                Logger.getLogger(DhisMonitorApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String queryAndSort(HttpClient httpClient) throws JsonProcessingException, IOException {
        String result = httpClient.call();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(result);
        SortJsonTree.sort(json);
        result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        return result;
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
                    .beginField()
                    .name("name")
                    .end()
                    .beginField()
                    .name("code")
                    .end()
                    .beginField()
                    .name("id")
                    .end()
                    .beginFilter()
                    .lhs("code")
                    .op("eq")
                    .value(code)
                    .end()
                    .build();
            HttpClient httpClient = query.toHttpClient();
            String result = queryAndSort(httpClient);
            writeFile(depObjectId, repo, result);
        }
    }

    private void processCategories(String code, String parentObjectId, Map<String, Set<String>> notifyMap, Set<String> processed, final Path repo) throws IOException {
        final String depObjectId = dhisUniqueObjectId(code, "categories");
        addDepNotifyRequest(notifyMap, depObjectId, parentObjectId);
        if (processed.add(depObjectId)) {
            DHISQuery query = dhisQueryBuilder()
                    .type("categories")
                    .beginField()
                    .name("name")
                    .end()
                    .beginField()
                    .name("code")
                    .end()
                    .beginField()
                    .name("id")
                    .end()
                    .beginField()
                    .name("categoryOptions")
                    .beginField() // 2
                    .name("name")
                    .end()
                    .beginField() // 2
                    .name("code")
                    .end()
                    .beginField() // 2
                    .name("id")
                    .end()
                    .end()
                    .beginFilter()
                    .lhs("code")
                    .op("eq")
                    .value(code)
                    .end()
                    .build();
            HttpClient httpClient = query.toHttpClient();
            String result = queryAndSort(httpClient);
            writeFile(depObjectId, repo, result);
        }
    }

    private void processDataElements(String code, String parentObjectId, Map<String, Set<String>> notifyMap, Set<String> processed, final Path repo) throws IOException {
        final String depObjectId = dhisUniqueObjectId(code, "dataElements");
        addDepNotifyRequest(notifyMap, depObjectId, parentObjectId);
        if (processed.add(depObjectId)) {
            DHISQuery query = dhisQueryBuilder()
                    .type("dataElements")
                    .beginField()
                    .name("name")
                    .end()
                    .beginField()
                    .name("id")
                    .end()
                    .beginField()
                    .name("code")
                    .end()
                    .beginField()
                    .name("categoryCombo") // 2
                    .beginField()
                    .name("categories")
                    .beginField() // 3
                    .name("name")
                    .end()
                    .beginField() // 3
                    .name("code")
                    .end()
                    .beginField() // 3
                    .name("id")
                    .end()
                    .end()
                    .end()
                    .beginFilter()
                    .lhs("code")
                    .op("eq")
                    .value(code)
                    .end()
                    .build();
            HttpClient httpClient = query.toHttpClient();
            String result = queryAndSort(httpClient);
            writeFile(depObjectId, repo, result);
            // parse json
            DocumentContext ctx = JsonPath.using(jsonPathConf).parse(result);
            // categories
            List<String> categories = ctx.read("$.dataElements[*].categoryCombo.categories[*].code");
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
