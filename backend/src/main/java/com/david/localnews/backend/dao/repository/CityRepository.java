package com.david.localnews.backend.dao.repository;

import java.util.Optional;

import com.david.localnews.backend.dao.entity.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @Query("""
    SELECT c FROM City c
    WHERE LOWER(c.name) LIKE LOWER(CONCAT(:q, '%'))
       OR LOWER(c.stateName) LIKE LOWER(CONCAT(:q, '%'))
       OR LOWER(CONCAT(c.name, ', ', c.stateId)) LIKE LOWER(CONCAT(:q, '%'))
    ORDER BY c.population DESC NULLS LAST, c.name ASC
    """)
    Page<City> searchPrefix(@Param("q") String q, Pageable pageable);

    @Query("""
    SELECT c FROM City c
    WHERE LOWER(c.name) = LOWER(:name)
      AND UPPER(c.stateId) = UPPER(:state)
    """)
    Optional<City> findExact(@Param("name") String name, @Param("state") String state);

    @Query("""
    SELECT c FROM City c
    WHERE LOWER(c.name) = LOWER(:name)
    ORDER BY c.population DESC NULLS LAST, c.name ASC
    """)
    Optional<City> findBestByName(@Param("name") String name);

    @Query("""
    SELECT c FROM City c
    WHERE c.population IS NOT NULL
    ORDER BY c.population DESC
    """)
    Page<City> topByPopulation(Pageable pageable);
}