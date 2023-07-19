package repository;

import model.Hit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface HitRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT COUNT(*) " +
            "FROM Hit AS h " +
            "WHERE h.timestamp > ?1 " +
            "AND h.timestamp < ?2 " +
            "AND h.uri = ?3")
    int countHitsWithNotUniqueIps(LocalDateTime start, LocalDateTime end, String uri);

    @Query("SELECT COUNT(*) " +
            "FROM Hit AS h " +
            "WHERE h.timestamp > ?1 " +
            "AND h.timestamp < ?2 " +
            "AND h.uri = ?3 " +
            "GROUP BY h.ip")
    int countHitsWithUniqueIps(LocalDateTime start, LocalDateTime end, String uri);

    @Query("SELECT h.app " +
            "FROM Hit AS h " +
            "WHERE uri = ?1")
    String getAppByUri(String uri);
}