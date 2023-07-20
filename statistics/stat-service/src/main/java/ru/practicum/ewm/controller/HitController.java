package ru.practicum.ewm.controller;

import ru.practicum.ewm.dto.Stats;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.model.Hit;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.HitService;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    public List<Stats> getStats(@RequestParam @NotNull String start,
                                @RequestParam @NotNull String end,
                                @RequestParam(required = false) String uris,
                                @RequestParam(defaultValue = "false") boolean uniqueIp) {
        return hitService.getStats(start, end, uris, uniqueIp);
    }


}
