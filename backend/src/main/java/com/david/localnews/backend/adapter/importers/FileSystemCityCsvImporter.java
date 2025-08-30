package com.david.localnews.backend.adapter.importers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import com.david.localnews.backend.dao.entity.City;
import com.david.localnews.backend.dao.repository.CityRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FileSystemCityCsvImporter implements CityCsvImporter {
    private final CityRepository cityRepository;

    public FileSystemCityCsvImporter(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    @Transactional
    public int importIfEmpty() {
        long count = cityRepository.count();
        if (count > 0) return 0;
        String projectRoot = System.getProperty("user.dir");
        String path = projectRoot.replace("\\", "/").replaceFirst("/backend$", "") + "/data/us_cities.csv";
        AtomicInteger inserted = new AtomicInteger();
        try (var reader = new BufferedReader(new InputStreamReader(new java.io.FileInputStream(path), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header == null || !header.toLowerCase().startsWith("city,")) throw new IllegalStateException("us_cities.csv: wrong or missing header line");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 6) continue;
                String city = p[0].trim();
                String stateId = p[1].trim();
                String stateName = p[2].trim();
                Double lat = parseDouble(p[3]);
                Double lon = parseDouble(p[4]);
                Integer population = parseInt(p[5]);
                if (city.isEmpty() || stateId.isEmpty() || stateName.isEmpty()) continue;
                City e = new City(null, city, stateId, stateName, lat, lon, population);
                cityRepository.save(e);
                inserted.incrementAndGet();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to import cities from " + path, e);
        }
        return inserted.get();
    }

    private Double parseDouble(String s) {
        try { return s == null || s.isBlank() ? null : Double.parseDouble(s); }
        catch (Exception e) { return null; }
    }

    private Integer parseInt(String s) {
        try { return s == null || s.isBlank() ? null : Integer.parseInt(s); }
        catch (Exception e) { return null; }
    }
}
