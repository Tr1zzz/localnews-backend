package com.david.localnews.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.david.localnews")
@EnableJpaRepositories(basePackages = "com.david.localnews.backend.dao.repository")
@EntityScan(basePackages = "com.david.localnews.backend.dao.entity")
public class BackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
}