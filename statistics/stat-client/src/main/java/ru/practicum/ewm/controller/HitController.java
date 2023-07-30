package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.service.HitClient;

import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class HitController {
    private final HitClient hitClient;

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam @NotNull String start,
                                           @RequestParam @NotNull String end,
                                           @RequestParam(required = false) String uris,
                                           @RequestParam(defaultValue = "false") boolean unique) {
        return hitClient.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    public ResponseEntity<Object> postHit(@RequestBody Hit hit) {
        return hitClient.postHit(hit);
    }
}
