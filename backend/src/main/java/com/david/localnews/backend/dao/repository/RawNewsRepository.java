package com.david.localnews.backend.dao.repository;

import java.time.Instant;
import java.util.List;

import com.david.localnews.backend.dao.entity.RawNews;
import com.david.localnews.backend.dao.entity.enums.RssFeedType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RawNewsRepository extends JpaRepository<RawNews, Long> {

    boolean existsByUrl(String url);

    long countByFeedType(RssFeedType type);

    long countByFetchedAtAfter(Instant after);

    @Query("""
    SELECT r FROM RawNews r
    WHERE NOT EXISTS (
      SELECT 1 FROM NewsItem n
      WHERE n.rawId = r.id
    )
    ORDER BY r.fetchedAt DESC
    """)
    List<RawNews> findTopNNotInNewsItem(Pageable pageable);
}