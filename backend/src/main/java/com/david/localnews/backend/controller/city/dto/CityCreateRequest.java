package com.david.localnews.backend.controller.city.dto;

import jakarta.validation.constraints.NotBlank;

public record CityCreateRequest(
        @NotBlank String name,
        String stateId,
        String stateName,
        Double lat,
        Double lon,
        Integer population
) {}