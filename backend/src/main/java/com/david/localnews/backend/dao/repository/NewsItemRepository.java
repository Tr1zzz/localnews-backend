package com.david.localnews.backend.dao.repository;

import com.david.localnews.backend.dao.entity.NewsItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsItemRepository extends JpaRepository<NewsItem, Integer> {

    boolean existsByRawId(Long rawId);

    Page<NewsItem> findByCityIdOrderByDecidedAtDesc(Long cityId, Pageable pageable);

    Page<NewsItem> findByIsLocalOrderByDecidedAtDesc(boolean isLocal, Pageable pageable);

    Page<NewsItem> findAllByOrderByDecidedAtDesc(Pageable pageable);
}
