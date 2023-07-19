package controller;

import dto.Stats;
import lombok.RequiredArgsConstructor;
import model.Hit;
import org.springframework.web.bind.annotation.*;
import service.HitService;

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
    public List<Stats> getStats(@RequestParam @NotNull LocalDateTime start,
                                @RequestParam @NotNull LocalDateTime end,
                                @RequestParam String[] uris,
                                @RequestParam(defaultValue = "false") boolean uniqueIp) {
        return hitService.getStats(start, end, uris, uniqueIp);
    }
}
