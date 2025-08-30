package com.david.localnews.service.city;

import java.util.List;

import com.david.localnews.backend.adapter.importers.CityCsvImporter;
import com.david.localnews.backend.dao.entity.City;
import com.david.localnews.backend.dao.repository.CityRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CityService {
    private final CityRepository cityRepository;
    private final CityCsvImporter cityCsvImporter;

    public CityService(CityRepository cityRepository, CityCsvImporter cityCsvImporter) {
        this.cityRepository = cityRepository;
        this.cityCsvImporter = cityCsvImporter;
    }

    @Transactional(readOnly = true)
    public List<City> search(String query, int limit) {
        String q = query == null ? "" : query.trim().toLowerCase();
        if (q.isEmpty()) return List.of();
        int clamped = Math.max(1, Math.min(limit, 25));
        return cityRepository.searchPrefix(q, PageRequest.of(0, clamped)).getContent();
    }

    @Transactional(readOnly = true)
    public List<City> topByPopulation(int limit) {
        int clamped = Math.max(1, limit);
        return cityRepository.topByPopulation(PageRequest.of(0, clamped)).getContent();
    }

    @Transactional
    public int importIfEmpty() {
        return cityCsvImporter.importIfEmpty();
    }

    public long count() {
        return cityRepository.count();
    }

}