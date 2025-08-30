package com.david.localnews.backend.adapter.llm;

public record LlmResult(String kind, String city, String state, int confidence) {
    public boolean isLocal() {
        return "local".equalsIgnoreCase(kind);
    }
}