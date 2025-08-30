package com.david.localnews.backend.controller.city.dto;

public record CityUpdateRequest(
        String name,
        String stateId,
        String stateName,
        Double lat,
        Double lon,
        Integer population
) {}