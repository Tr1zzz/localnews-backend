package com.david.localnews.backend.controller.city;

import com.david.localnews.backend.controller.city.dto.CityResponse;
import com.david.localnews.backend.dao.entity.City;
import com.david.localnews.service.city.CityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cities")
@CrossOrigin
public class CityController {
    private final CityService cityService;
    private final ObjectMapper objectMapper;

    public CityController(CityService cityService, ObjectMapper objectMapper) {
        this.cityService = cityService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public List<CityResponse> search(@RequestParam("query") String query,
                                     @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<City> cities = cityService.search(query, limit);
        return cities.stream().map(c -> objectMapper.convertValue(c, CityResponse.class)).toList();
    }

}