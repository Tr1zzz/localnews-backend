package com.david.localnews.backend.controller.news.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record NewsResponse(
        Integer id,
        String title,
        String summary,
        String url,
        String source,
        @JsonProperty("isLocal")
        @JsonAlias({"local"})
        boolean isLocal,
        Long cityId,
        Integer confidence,
        Instant decidedAt
) {}