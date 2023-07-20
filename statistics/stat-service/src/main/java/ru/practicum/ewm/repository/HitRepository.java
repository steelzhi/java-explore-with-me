package ru.practicum.ewm.repository;

import ru.practicum.ewm.model.Hit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT COUNT(*) " +
            "FROM Hit AS h " +
            "WHERE h.timestamp > ?1 " +
            "AND h.timestamp < ?2 " +
            "AND h.uri = ?3 " +
            "GROUP BY h.ip")
    int countHitsWithUriAndUniqueIps(LocalDateTime start, LocalDateTime end, String uri);

    @Query("SELECT COUNT(*) " +
            "FROM Hit AS h " +
            "WHERE h.timestamp > ?1 " +
            "AND h.timestamp < ?2 " +
            "AND h.uri = ?3")
    int countHitsWithUriAndNoUniqueIps(LocalDateTime start, LocalDateTime end, String uri);

    @Query("SELECT COUNT(*) " +
            "FROM Hit AS h " +
            "WHERE h.timestamp > ?1 " +
            "AND h.timestamp < ?2 " +
            "GROUP BY h.ip")
    int countHitsWithUniqueIpsAndNoUris(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(*) " +
            "FROM Hit AS h " +
            "WHERE h.timestamp > ?1 " +
            "AND h.timestamp < ?2")
    int countHitsWithNoUniqueIpsAndNoUris(LocalDateTime start, LocalDateTime end);

    @Query("SELECT h.app " +
            "FROM Hit AS h " +
            "WHERE uri = ?1")
    String getAppByUri(String uri);
}