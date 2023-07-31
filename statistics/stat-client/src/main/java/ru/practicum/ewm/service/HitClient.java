package ru.practicum.ewm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.client.BaseClient;
import ru.practicum.ewm.exception.CodingException;
import ru.practicum.ewm.model.Hit;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class HitClient extends BaseClient {

    @Autowired
    public HitClient(@Value("http://localhost:9091") String serverUrl, RestTemplateBuilder builder) {
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
                                           boolean unique) {
        Map<String, Object> parameters = new HashMap<>();

        try {
            if (start != null) {
                start = URLEncoder.encode(start, "utf-8");
            }
            if (end != null) {
                end = URLEncoder.encode(end, "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            throw new CodingException("Ошибка кодирования дат");
        }

        parameters.put("start", start);
        parameters.put("end", end);
        parameters.put("unique", unique);

        if (uris != null) {
            parameters.put("uris", uris);
            return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        } else {
            return get("/stats?start={start}&end={end}&unique={unique}", parameters);
        }
    }

    public ResponseEntity<Object> postHit(Hit hit) {
        return post("/hit", hit);
    }
}
