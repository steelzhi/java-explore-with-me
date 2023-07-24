package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.Stats;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.repository.HitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    @Override
    public List<Stats> getStats(
            String start,
            String end,
            String[] uris,
            boolean unique) {
        List<Stats> stats;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTimeStart = LocalDateTime.parse(start, formatter);
        LocalDateTime dateTimeEnd = LocalDateTime.parse(end, formatter);

        List<String> allUris = new ArrayList<>();

        if (uris == null) {
            List<Hit> allHits = hitRepository.findAll();
            for (Hit hit : allHits) {
                allUris.add(hit.getUri());
            }
        } else {
            allUris = Arrays.asList(uris);
        }

        if (unique) {
            stats = hitRepository.getStatsWithUniqueIps(dateTimeStart, dateTimeEnd, allUris);
        } else {
            stats = hitRepository.getStatsWithNoUniqueIps(dateTimeStart, dateTimeEnd, allUris);
        }

        stats.sort((stats1, stats2) -> {
            if (stats1.getHits() < stats2.getHits()) {
                return 1;
            } else {
                return -1;
            }
        });

        return stats;
    }

    @Override
    public Hit saveHit(Hit hit) {
        Hit savedHit = hitRepository.save(hit);
        return savedHit;
    }
}
