package com.david.localnews.service.news;

import com.david.localnews.backend.dao.entity.NewsItem;
import com.david.localnews.backend.dao.repository.NewsItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class NewsService {
    private final NewsItemRepository newsItemRepository;

    public NewsService(NewsItemRepository newsItemRepository) {
        this.newsItemRepository = newsItemRepository;
    }

    public Page<NewsItem> get(Long cityId, String scope, Pageable pageable) {
        if (cityId != null) return newsItemRepository.findByCityIdOrderByDecidedAtDesc(cityId, pageable);
        String s = scope == null ? "local" : scope.toLowerCase(Locale.ROOT);
        return switch (s) {
            case "global" -> newsItemRepository.findByIsLocalOrderByDecidedAtDesc(false, pageable);
            case "all" -> newsItemRepository.findAllByOrderByDecidedAtDesc(pageable);
            default -> newsItemRepository.findByIsLocalOrderByDecidedAtDesc(true, pageable);
        };
    }

}