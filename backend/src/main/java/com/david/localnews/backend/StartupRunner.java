package com.david.localnews.backend;

import com.david.localnews.service.city.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    public StartupRunner(CityService cityService) {
        this.cityService = cityService;
    }

    @Override
    public void run(String... args) {
        int imported = cityService.importIfEmpty();
        if (imported > 0) logger.info("Imported {} cities on startup", imported);
        else logger.info("City table already populated, skipping import");
    }

    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);
    private final CityService cityService;
}