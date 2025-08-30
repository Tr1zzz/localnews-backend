package com.david.localnews.backend.controller.news.dto;

public record ClassificationResponse(
        int classified,
        long totalItems
) {}