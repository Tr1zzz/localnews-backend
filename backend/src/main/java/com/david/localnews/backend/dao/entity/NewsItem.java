package com.david.localnews.backend.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "news_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "raw_id", unique = true, nullable = false)
    private Long rawId;

    @Column(length = 512, nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(length = 1024, nullable = false)
    private String url;

    @Column(length = 256)
    private String source;

    @Column(name = "is_local", nullable = false)
    private boolean isLocal;

    @Column(name = "city_id")
    private Long cityId;

    @Column(nullable = false)
    private Integer confidence = 0;

    @CreationTimestamp
    @Column(name = "decided_at", nullable = false, updatable = false)
    private Instant decidedAt;
}