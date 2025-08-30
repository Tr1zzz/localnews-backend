package com.david.localnews.backend.adapter.llm;

public interface NewsClassifier {
    Classification classify(String title, String summary, String source);

    record Classification(boolean isLocal, String city, String state, int confidence) {}
}