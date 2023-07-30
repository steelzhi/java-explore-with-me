package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.Stats;
import ru.practicum.ewm.exception.CodingException;
import ru.practicum.ewm.exception.IncorrectDateException;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.repository.HitRepository;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
    @Transactional
    public List<Stats> getStats(
            String start,
            String end,
            String[] uris,
            boolean unique) {
        List<Stats> stats;

        String decodedDateStart = null;
        String decodedDateEnd = null;
        try {
            if (start != null) {
                decodedDateStart = URLDecoder.decode(start, "utf-8");
            }
            if (end != null) {
                decodedDateEnd = URLDecoder.decode(end, "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            throw new CodingException("Ошибка декодирования дат");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime dateTimeStart = null;
        LocalDateTime dateTimeEnd = null;
        if (decodedDateStart != null) {
            dateTimeStart = LocalDateTime.parse(decodedDateStart, formatter);
        }
        if (decodedDateEnd != null) {
            dateTimeEnd = LocalDateTime.parse(decodedDateEnd, formatter);
        }

        checkIfDatesAreNotCorrect(dateTimeStart, dateTimeEnd);

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
    @Transactional
    public Hit saveHit(Hit hit) {
        Hit savedHit = hitRepository.save(hit);
        return savedHit;
    }

    private void checkIfDatesAreNotCorrect(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd) {
        if ((dateTimeStart == null && dateTimeEnd != null)
                || (dateTimeEnd == null && dateTimeStart != null)
                || (dateTimeStart != null && dateTimeEnd != null && dateTimeStart.isAfter(dateTimeEnd))) {
            throw new IncorrectDateException("Введен некорректный диапазон дат");
        }
    }
}
