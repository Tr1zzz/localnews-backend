package com.david.localnews.backend.dao.entity;

import com.david.localnews.backend.dao.entity.enums.RssFeedType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "news_raw",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_news_raw_url", columnNames = {"url"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String title;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(nullable = false, length = 1024)
    private String url;

    @Column(nullable = false, length = 256)
    private String source;

    private Instant publishedAt;

    @Column(nullable = false)
    private Instant fetchedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RssFeedType feedType;
}