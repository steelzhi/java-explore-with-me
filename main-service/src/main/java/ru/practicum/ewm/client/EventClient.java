package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.model.Hit;

import java.util.HashMap;
import java.util.Map;

@Service
public class EventClient extends BaseClient {

    @Autowired
    public EventClient(@Value("${stat-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<Object> getStats(String start,
                                           String end,
                                           String[] uris,
                                           boolean unique) {
        Map<String, Object> parameters = new HashMap<>();

        StringBuilder path = new StringBuilder("/stats?");
        if (start != null) {
            path.append("start={start}");
            parameters.put("start", start);
        }
        if (end != null) {
            if (path.charAt(path.length() - 1) == '?') {
                path.append("end={end}");
            } else {
                path.append("&end={end}");
            }
            parameters.put("end", end);
        }
        if (uris != null) {
            if (path.charAt(path.length() - 1) == '?') {
                path.append("uris={uris}");
            } else {
                path.append("&uris={uris}");
            }
            parameters.put("uris", uris);
        }
        if (path.charAt(path.length() - 1) == '?') {
            path.append("unique={unique}");
        } else {
            path.append("&unique={unique}");
        }
        parameters.put("unique", unique);

        return get(path.toString(), parameters);
    }

    @Transactional
    public ResponseEntity<Object> postHit(Hit hit) {
        return post("/hit", hit);
    }
}
