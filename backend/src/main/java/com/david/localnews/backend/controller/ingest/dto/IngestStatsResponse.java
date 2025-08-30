package com.david.localnews.backend.controller.ingest.dto;

public record IngestStatsResponse(long total, long local, long global, long lastHour) {}
