package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.model.Hit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.HitClient;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
                                           @RequestParam(defaultValue = "false") boolean uniqueIp) {
        ResponseEntity<Object> re = hitClient.getStats(start, end, uris, uniqueIp);
        return re;
    }


    @PostMapping("/hit")
    public ResponseEntity<Object> postHit(@RequestBody Hit hit) {
        return hitClient.postHit(hit);
    }
}
