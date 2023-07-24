package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.Stats;
import ru.practicum.ewm.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ru.practicum.ewm.dto.Stats(h.app, h.uri, count(h)) " +
            "FROM Hit AS h " +
            "WHERE h.timestamp > ?1 " +
            "AND h.timestamp < ?2 " +
            "AND h.uri IN ?3 " +
            "GROUP BY h.app, h.uri")
    List<Stats> getStatsWithNoUniqueIps(LocalDateTime start, LocalDateTime end, List<String> allUris);

    @Query("SELECT new ru.practicum.ewm.dto.Stats(h.app, h.uri, count(DISTINCT h.ip)) " +
            "FROM Hit AS h " +
            "WHERE h.timestamp > ?1 " +
            "AND h.timestamp < ?2 " +
            "AND h.uri IN ?3 " +
            "GROUP BY h.app, h.uri")
    List<Stats> getStatsWithUniqueIps(LocalDateTime start, LocalDateTime end, List<String> allUris);
}