package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.Stats;
import ru.practicum.ewm.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    List<Stats> getStats(String start, String end, String uris, boolean uniqueIp);

    Hit saveHit(Hit hit);
}
