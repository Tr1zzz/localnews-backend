package com.david.localnews.service.ingest;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.david.localnews.backend.adapter.rss.RssClient;
import com.david.localnews.backend.dao.entity.City;
import com.david.localnews.backend.dao.entity.RawNews;
import com.david.localnews.backend.dao.entity.enums.RssFeedType;
import com.david.localnews.backend.dao.repository.CityRepository;
import com.david.localnews.backend.dao.repository.RawNewsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class RssIngestionService {
    private final RssClient rssClient;
    private final RawNewsRepository rawNewsRepository;
    private final CityRepository cityRepository;
    private final int topCities;
    private final long delayMs;
    private final int localMaxPerFeed;
    private final int globalMaxPerFeed;
    private final String googleCityTemplate;
    private final List<String> globalFeeds;

    public RssIngestionService(RssClient rssClient,
                               RawNewsRepository rawNewsRepository,
                               CityRepository cityRepository,
                               @Value("${app.ingest.topCities:40}") int topCities,
                               @Value("${app.ingest.delayMsBetweenCalls:200}") long delayMs,
                               @Value("${app.ingest.local.maxPerFeed:5}") int localMaxPerFeed,
                               @Value("${app.ingest.global.maxPerFeed:5}") int globalMaxPerFeed,
                               @Value("${app.ingest.templates.googleCity:https://news.google.com/rss/search?q=%s&hl=en-US&gl=US&ceid=US:en}") String googleCityTemplate,
                               @Value("${app.rss.global:}") String globalFeedsRaw) {
        this.rssClient = rssClient;
        this.rawNewsRepository = rawNewsRepository;
        this.cityRepository = cityRepository;
        this.topCities = topCities;
        this.delayMs = delayMs;
        this.localMaxPerFeed = localMaxPerFeed;
        this.globalMaxPerFeed = globalMaxPerFeed;
        this.googleCityTemplate = googleCityTemplate;
        this.globalFeeds = parseGlobalFeeds(globalFeedsRaw);
    }

    public int ingestAll(Integer overrideTopCities, Integer overrideLocalMax, Integer overrideGlobalMax) {
        int citiesToTake = overrideTopCities != null ? overrideTopCities : topCities;
        int localLimit = overrideLocalMax != null ? overrideLocalMax : localMaxPerFeed;
        int globalLimit = overrideGlobalMax != null ? overrideGlobalMax : globalMaxPerFeed;
        List<String> localFeeds = buildCityFeeds(citiesToTake);
        int inserted = 0;
        inserted += ingestGroup(localFeeds, RssFeedType.LOCAL_CANDIDATE, localLimit);
        inserted += ingestGroup(globalFeeds, RssFeedType.GLOBAL_CANDIDATE, globalLimit);
        return inserted;
    }

    public long totalRaw() {
        return rawNewsRepository.count();
    }

    public long countLocalCandidates() {
        return rawNewsRepository.countByFeedType(RssFeedType.LOCAL_CANDIDATE);
    }

    public long countGlobalCandidates() {
        return rawNewsRepository.countByFeedType(RssFeedType.GLOBAL_CANDIDATE);
    }

    public long countFetchedLastSeconds(int seconds) {
        return rawNewsRepository.countByFetchedAtAfter(Instant.now().minusSeconds(seconds));
    }

    private List<String> buildCityFeeds(int limit) {
        var page = cityRepository.topByPopulation(PageRequest.of(0, Math.max(1, limit)));
        List<City> cities = page.getContent();
        List<String> urls = new ArrayList<>(cities.size());
        for (City c : cities) {
            String q = (c.getName() + " " + c.getStateId()).trim();
            String encoded = URLEncoder.encode(q, StandardCharsets.UTF_8);
            urls.add(googleCityTemplate.formatted(encoded));
        }
        return urls;
    }

    private int ingestGroup(List<String> feeds, RssFeedType type, int maxPerFeed) {
        if (feeds == null || feeds.isEmpty()) return 0;
        int sum = 0;
        for (String raw : feeds) {
            String url = raw == null ? "" : raw.trim();
            if (url.isEmpty()) continue;
            try {
                List<RawNews> list = rssClient.fetchOne(url, type, maxPerFeed);
                for (RawNews r : list) {
                    if (r == null) continue;
                    if (rawNewsRepository.existsByUrl(r.getUrl())) continue;
                    rawNewsRepository.save(r);
                    sum++;
                }
            } catch (Exception ignored) {
            }
            if (delayMs > 0) {
                try { Thread.sleep(delayMs); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
            }
        }
        return sum;
    }

    private List<String> parseGlobalFeeds(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        String[] parts = raw.split("[,;\\s]+");
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            String s = p.trim();
            if (!s.isEmpty()) list.add(s);
        }
        return list;
    }

}