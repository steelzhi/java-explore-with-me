package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.Stats;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.model.Hit;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.repository.HitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    @Override
    public List<Stats> getStats(
            String start,
            String end,
            String uris,
            boolean uniqueIp) {
        List<Stats> stats = new ArrayList<>();
        int hits;

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
            String[] urisArray = uris.split(" ");
            for (String uri : urisArray) {
                allUris.add(uri);
            }
        }

        for (String uri : allUris) {
            if (uniqueIp) {
                List<Hit> allHits = hitRepository.findAll();
                hits = hitRepository.countHitsWithUriAndUniqueIps(dateTimeStart, dateTimeEnd, uri);
            } else {
                hits = hitRepository.countHitsWithUriAndNoUniqueIps(dateTimeStart, dateTimeEnd, uri);
            }
            String app = getAppByUri(uri);
            stats.add(new Stats(app, uri, hits));
        }

        return stats;
    }

    @Override
    public Hit saveHit(Hit hit) {
        //hit.setTimestamp(LocalDateTime.now());
        Hit savedHit = hitRepository.save(hit);
        return savedHit;
    }

    private String getAppByUri(String uri) {
        return hitRepository.getAppByUri(uri);
    }
}
