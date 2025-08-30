package com.david.localnews.service.news;


import java.time.Instant;
import java.util.List;

import com.david.localnews.backend.adapter.llm.NewsClassifier;
import com.david.localnews.backend.dao.entity.NewsItem;
import com.david.localnews.backend.dao.entity.RawNews;
import com.david.localnews.backend.dao.repository.CityRepository;
import com.david.localnews.backend.dao.repository.NewsItemRepository;
import com.david.localnews.backend.dao.repository.RawNewsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ClassificationService {
    private final NewsClassifier classifier;
    private final RawNewsRepository rawNewsRepository;
    private final NewsItemRepository newsItemRepository;
    private final CityRepository cityRepository;

    public ClassificationService(NewsClassifier classifier,
                                 RawNewsRepository rawNewsRepository,
                                 NewsItemRepository newsItemRepository,
                                 CityRepository cityRepository) {
        this.classifier = classifier;
        this.rawNewsRepository = rawNewsRepository;
        this.newsItemRepository = newsItemRepository;
        this.cityRepository = cityRepository;
    }

    public int classifyBatch(int limit) {
        int size = Math.max(1, Math.min(limit, 200));
        List<RawNews> raws = rawNewsRepository.findTopNNotInNewsItem(PageRequest.of(0, size));
        int inserted = 0;
        for (RawNews r : raws) {
            if (r == null) continue;
            if (newsItemRepository.existsByRawId(r.getId())) continue;
            NewsClassifier.Classification res = classifier.classify(safe(r.getTitle()), safe(r.getSummary()), safe(r.getSource()));
            Long cityId = null;
            if (res.isLocal()) {
                String city = safe(res.city());
                String st = safe(res.state());
                if (!city.isEmpty() && st.length() == 2) cityId = cityRepository.findExact(city, st).map(c -> c.getId()).orElse(null);
                if (cityId == null && !city.isEmpty()) cityId = cityRepository.findBestByName(city).map(c -> c.getId()).orElse(null);
            }
            NewsItem ni = new NewsItem(null, r.getId(), r.getTitle(), r.getSummary(), r.getUrl(), r.getSource(),
                    res.isLocal(), cityId, Math.max(0, Math.min(res.confidence(), 100)), Instant.now());
            newsItemRepository.save(ni);
            inserted++;
        }
        return inserted;
    }

    public long totalItems() {
        return newsItemRepository.count();
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

}
