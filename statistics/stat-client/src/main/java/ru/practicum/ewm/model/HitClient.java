package ru.practicum.ewm.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.client.BaseClient;
import ru.practicum.ewm.model.Hit;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class HitClient extends BaseClient {

    @Autowired
    public HitClient(@Value("${stat-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getStats(String start,
                                           String end,
                                           String uris,
                                           boolean uniqueIp) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        parameters.put("uniqueIp", uniqueIp);

        if (uris != null) {
            parameters.put("uris", uris);
            return get("/stats?start={start}&end={end}&uris={uris}&uniqueIp={uniqueIp}", parameters);
        } else {
            return get("/stats?start={start}&end={end}&uniqueIp={uniqueIp}", parameters);
        }
    }

    public ResponseEntity<Object> postHit(Hit hit) {
        return post("/hit", hit);
    }

}