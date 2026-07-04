package com.example.wefluxconsumer.controller;

import com.example.wefluxconsumer.client.WikimediaRecentChangeClient;
import com.example.wefluxconsumer.model.RecentChange;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class WikipediaController {

    private final WikimediaRecentChangeClient client;

    public WikipediaController(WikimediaRecentChangeClient client) {
        this.client = client;
    }

    @CrossOrigin(origins = {"http://localhost:8081", "http://127.0.0.1:8081"})
    @GetMapping(value = "/api/wikipedia/recent-changes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<RecentChange> recentChanges(
            @RequestParam(required = false) String wiki,
            @RequestParam(defaultValue = "false") boolean includeBots,
            @RequestParam(required = false) Long limit
    ) {
        Flux<RecentChange> stream = client.streamRecentChanges()
                .filter(change -> wiki == null || wiki.equalsIgnoreCase(change.wiki()))
                .filter(change -> includeBots || !Boolean.TRUE.equals(change.bot()));

        if (limit == null) {
            return stream;
        }

        return stream.take(limit);
    }
}
