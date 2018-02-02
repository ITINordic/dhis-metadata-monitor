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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author cliffordc
 */
// Based of json-sort https://github.com/npeters/json-sort
public class SortJsonTree {

    public static void sort(JsonNode node) {
        if (node.isArray()) {
            sortArray(node);
        } else if (node.isObject()) {
            sortObject(node);
        }
    }

    private static void sortArray(JsonNode node) {
        for (JsonNode jsonNode : node) {
            sort(jsonNode);
        }
        ArrayNode arrayNode = (ArrayNode) node;
        ArrayList<JsonNode> nodes = Lists.newArrayList(arrayNode.elements());
        Collections.sort(nodes, new DhisIdComparotor());
        arrayNode.removeAll();
        arrayNode.addAll(nodes);
    }

    private static void sortObject(JsonNode node) {
        List<String> asList = Lists.newArrayList(node.fieldNames());
        Collections.sort(asList);
        LinkedHashMap<String, JsonNode> map = new LinkedHashMap<String, JsonNode>();
        for (String f : asList) {

            JsonNode value = node.get(f);
            sort(value);
            map.put(f, value);
        }
        final ObjectNode objNode = (ObjectNode) node;
        objNode.removeAll();
        objNode.setAll(map);
    }

    private static class DhisIdComparotor implements Comparator<JsonNode> {

        public DhisIdComparotor() {
        }

        @Override
        public int compare(JsonNode node1, JsonNode node2) {
            final int nullNode = 0;
            final int valueNode = 1;
            final int objNode = 2;
            final int arrayNode = 3;
            // null < value < object < array
            int node1TypeVal = nullNode;
            int node2TypeVal = nullNode;
            if (node1 == null) {
                node1TypeVal = nullNode;
            }
            if (node1.isValueNode()) {
                node1TypeVal = valueNode;
            }
            if (node1.isObject()) {
                node1TypeVal = objNode;
            }
            if (node1.isArray()) {
                node1TypeVal = arrayNode;
            }
            if (node2 == null) {
                node2TypeVal = nullNode;
            }
            if (node2.isValueNode()) {
                node2TypeVal = valueNode;
            }
            if (node2.isObject()) {
                node2TypeVal = objNode;
            }
            if (node2.isArray()) {
                node2TypeVal = arrayNode;
            }
            if (node1TypeVal == node2TypeVal) {
                switch (node1TypeVal) {
                    case valueNode:
                        return compValue(node1, node2);
                    case objNode:
                        return compObject(node1, node2);
                    case arrayNode:
                        return compArray(node1, node2);
                    default:
                        return 0;
                }
            } else {
                return Integer.compare(node1TypeVal, node2TypeVal);
            }
        }

        private int compValue(JsonNode o1, JsonNode o2) {

            if (o1.isNull()) {
                return -1;
            }

            if (o2.isNull()) {
                return 1;
            }

            if (o1.isNumber() && o2.isNumber()) {
                return o1.bigIntegerValue().compareTo(o2.bigIntegerValue());
            }

            return o1.asText().compareTo(o2.asText());
        }

        private int compArray(JsonNode o1, JsonNode o2) {

            int c = ((ArrayNode) o1).size() - ((ArrayNode) o2).size();
            if (c != 0) {
                return c;
            }
            for (int i = 0; i < ((ArrayNode) o1).size(); i++) {
                c = compare(o1.get(i), o2.get(i));
                if (c != 0) {
                    return c;
                }
            }

            return 0;
        }

        private int compObject(JsonNode o1, JsonNode o2) {

            String id1 = o1.get("id") == null ? null : o1.get("id").asText();
            String id2 = o2.get("id") == null ? null : o2.get("id").asText();
            if (id1 != null) {
                int c = id1.compareTo(id2);
                if (c != 0) {
                    return c;
                }
            }
            int c = ((ObjectNode) o1).size() - ((ObjectNode) o2).size();
            if (c != 0) {
                return c;
            }

            Iterator<String> fieldNames1 = ((ObjectNode) o1).fieldNames();
            Iterator<String> fieldNames2 = ((ObjectNode) o2).fieldNames();
            for (; fieldNames1.hasNext();) {
                String f = fieldNames1.next();

                c = f.compareTo(fieldNames2.next());
                if (c != 0) {
                    return c;
                }

                JsonNode n1 = o1.get(f);
                JsonNode n2 = o2.get(f);
                c = compare(n1, n2);
                if (c != 0) {
                    return c;
                }
            }
            return 0;
        }
    }
}
