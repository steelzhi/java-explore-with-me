package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Hit;

import java.time.LocalDateTime;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT COUNT(DISTINCT h.ip) " +
            "FROM Hit AS h " +
            "WHERE h.timestamp > ?1 " +
            "AND h.timestamp < ?2 " +
            "AND h.uri = ?3 ")
    int countHitsWithUriAndUniqueIps(LocalDateTime start, LocalDateTime end, String uri);

    @Query("SELECT COUNT(*) " +
            "FROM Hit AS h " +
            "WHERE h.timestamp > ?1 " +
            "AND h.timestamp < ?2 " +
            "AND h.uri = ?3")
    int countHitsWithUriAndNoUniqueIps(LocalDateTime start, LocalDateTime end, String uri);

    @Query("SELECT h.app " +
            "FROM Hit AS h " +
            "WHERE uri = ?1 " +
            "GROUP BY h.app")
    String getAppByUri(String uri);
}