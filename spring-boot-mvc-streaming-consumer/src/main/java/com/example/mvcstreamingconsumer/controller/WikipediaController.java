package com.example.mvcstreamingconsumer.controller;

import com.example.mvcstreamingconsumer.client.WikimediaRecentChangeStreamingClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
public class WikipediaController {

    private final WikimediaRecentChangeStreamingClient client;

    public WikipediaController(WikimediaRecentChangeStreamingClient client) {
        this.client = client;
    }

    @CrossOrigin(origins = {"http://localhost:8081", "http://127.0.0.1:8081"})
    @GetMapping(value = "/api/wikipedia/recent-changes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> recentChanges(
            @RequestParam(required = false) String wiki,
            @RequestParam(defaultValue = "false") boolean includeBots,
            @RequestParam(required = false) Long limit
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header("X-Accel-Buffering", "no")
                .body(client.streamRecentChanges(wiki, includeBots, limit));
    }
}
