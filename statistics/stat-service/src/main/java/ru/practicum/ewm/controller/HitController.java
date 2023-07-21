package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.Stats;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.service.HitService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HitController {
    private final HitService hitService;

    @PostMapping("/hit")
    public Hit postHit(@RequestBody Hit hit) {
        return hitService.saveHit(hit);
    }

    @GetMapping("/stats")
    public List<Stats> getStats(@RequestParam String start,
                                @RequestParam String end,
                                @RequestParam(required = false) String uris,
                                @RequestParam Boolean unique) {
        return hitService.getStats(start, end, uris, unique);
    }


}
