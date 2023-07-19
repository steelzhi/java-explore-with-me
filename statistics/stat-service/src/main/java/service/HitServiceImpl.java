package service;

import dto.Stats;
import lombok.RequiredArgsConstructor;
import model.Hit;
import org.springframework.stereotype.Service;
import repository.HitRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    @Override
    public List<Stats> getStats(
            LocalDateTime start,
            LocalDateTime end,
            String[] uris,
            boolean uniqueIp) {
        List<Stats> stats = new ArrayList<>();
        int hits;

        for (String uri : uris) {
            if (uniqueIp) {
                hits = hitRepository.countHitsWithUniqueIps(start, end, uri);
            } else {
                hits = hitRepository.countHitsWithNotUniqueIps(start, end, uri);
            }
            String app = getAppByUri(uri);
            stats.add(new Stats(app, uri, hits));
        }

        return stats;
    }

    @Override
    public Hit saveHit(Hit hit) {
        Hit savedHit = hitRepository.save(hit);
        return savedHit;
    }

    private String getAppByUri(String uri) {
        return hitRepository.getAppByUri(uri);
    }
}
