package service;

import dto.Stats;
import model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    List<Stats> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean uniqueIp);

    Hit saveHit(Hit hit);
}
