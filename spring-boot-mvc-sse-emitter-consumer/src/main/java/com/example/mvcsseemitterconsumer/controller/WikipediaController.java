package com.example.mvcsseemitterconsumer.controller;

import com.example.mvcsseemitterconsumer.api.WikipediaApi;
import com.example.mvcsseemitterconsumer.client.WikimediaRecentChangeSseEmitterClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class WikipediaController implements WikipediaApi {

    private final WikimediaRecentChangeSseEmitterClient client;

    public WikipediaController(WikimediaRecentChangeSseEmitterClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<SseEmitter> streamRecentChanges(
            String wiki,
            Boolean includeBots,
            Long limit
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header("X-Accel-Buffering", "no")
                .body(client.streamRecentChanges(wiki, Boolean.TRUE.equals(includeBots), limit));
    }
}
