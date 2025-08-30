package com.david.localnews.backend.controller.news;

import com.david.localnews.backend.controller.news.dto.ClassificationResponse;
import com.david.localnews.backend.controller.news.dto.NewsResponse;
import com.david.localnews.backend.dao.entity.NewsItem;
import com.david.localnews.service.news.ClassificationService;
import com.david.localnews.service.news.NewsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController {
    private final NewsService newsService;
    private final ClassificationService classificationService;
    private final ObjectMapper objectMapper;

    public NewsController(NewsService newsService, ClassificationService classificationService, ObjectMapper objectMapper) {
        this.newsService = newsService;
        this.classificationService = classificationService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public List<NewsResponse> byCity(@RequestParam(required = false) Long cityId,
                                     @RequestParam(defaultValue = "local") String scope,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        Page<NewsItem> result = newsService.get(cityId, scope, PageRequest.of(Math.max(0, page), Math.min(size, 100)));
        return result.getContent().stream().map(n -> objectMapper.convertValue(n, NewsResponse.class)).toList();
    }

    @PostMapping("/classify")
    public ClassificationResponse classify(@RequestParam(defaultValue = "50") int limit) {
        int classified = classificationService.classifyBatch(Math.max(1, Math.min(limit, 200)));
        long totalItems = classificationService.totalItems();
        return new ClassificationResponse(classified, totalItems);
    }

}
