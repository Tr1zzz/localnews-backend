package com.david.localnews.backend.controller.city.dto;

public record CityResponse(
        Long id,
        String name,
        String stateId,
        String stateName,
        Double lat,
        Double lon,
        Integer population
) {}