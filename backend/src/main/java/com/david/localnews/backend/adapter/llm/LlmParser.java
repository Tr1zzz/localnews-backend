package com.david.localnews.backend.adapter.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LlmParser {
    private static final ObjectMapper M = new ObjectMapper();

    public static LlmResult parse(String json) {
        try {
            JsonNode n = M.readTree(json);
            String kind = text(n, "kind");
            String city = text(n, "city");
            String state = text(n, "state");
            int conf = n.has("confidence") ? n.get("confidence").asInt(0) : 0;
            return new LlmResult(kind, city, state, conf);
        } catch (Exception e) {
            return new LlmResult("global", "", "", 0);
        }
    }

    private static String text(JsonNode n, String k) {
        return n.has(k) && !n.get(k).isNull() ? n.get(k).asText("").trim() : "";
    }
}