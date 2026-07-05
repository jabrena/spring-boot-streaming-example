package com.example.wefluxconsumer.controller;

import com.example.wefluxconsumer.api.WikipediaApi;
import com.example.wefluxconsumer.client.WikimediaRecentChangeClient;
import com.example.wefluxconsumer.model.RecentChange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class WikipediaController implements WikipediaApi {

    private final WikimediaRecentChangeClient client;

    public WikipediaController(WikimediaRecentChangeClient client) {
        this.client = client;
    }

    @Override
    public Mono<ResponseEntity<?>> streamRecentChanges(
            String wiki,
            Boolean includeBots,
            Long limit
    ) {
        Flux<RecentChange> stream = client.streamRecentChanges()
                .filter(change -> wiki == null || wiki.equalsIgnoreCase(change.wiki()))
                .filter(change -> Boolean.TRUE.equals(includeBots) || !Boolean.TRUE.equals(change.bot()));

        if (limit != null) {
            stream = stream.take(limit);
        }

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header("X-Accel-Buffering", "no")
                .body(stream));
    }
}
