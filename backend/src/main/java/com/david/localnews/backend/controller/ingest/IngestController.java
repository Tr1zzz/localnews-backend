package com.david.localnews.backend.controller.ingest;

import com.david.localnews.backend.controller.ingest.dto.IngestRunResponse;
import com.david.localnews.backend.controller.ingest.dto.IngestStatsResponse;
import com.david.localnews.service.ingest.RssIngestionService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingest")
@CrossOrigin
public class IngestController {
    private final RssIngestionService ingestionService;
    public IngestController(RssIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/run")
    public IngestRunResponse run(@RequestParam(required = false) Integer topCities,
                                 @RequestParam(required = false) Integer maxLocal,
                                 @RequestParam(required = false) Integer maxGlobal) {
        int inserted = ingestionService.ingestAll(topCities, maxLocal, maxGlobal);
        long total = ingestionService.totalRaw();
        return new IngestRunResponse(inserted, total);
    }

    @GetMapping("/stats")
    public IngestStatsResponse stats() {
        long total = ingestionService.totalRaw();
        long local = ingestionService.countLocalCandidates();
        long global = ingestionService.countGlobalCandidates();
        long lastHour = ingestionService.countFetchedLastSeconds(3600);
        return new IngestStatsResponse(total, local, global, lastHour);
    }

}