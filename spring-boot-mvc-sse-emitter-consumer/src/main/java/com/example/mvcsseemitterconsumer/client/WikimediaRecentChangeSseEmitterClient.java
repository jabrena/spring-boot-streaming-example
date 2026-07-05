package com.example.mvcsseemitterconsumer.client;

import com.example.mvcsseemitterconsumer.config.WikimediaProperties;
import com.example.mvcsseemitterconsumer.model.RecentChange;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class WikimediaRecentChangeSseEmitterClient {

    private final WikimediaProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final ExecutorService executorService;

    public WikimediaRecentChangeSseEmitterClient(
            WikimediaProperties properties,
            ObjectMapper objectMapper,
            ExecutorService executorService
    ) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.executorService = executorService;
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public SseEmitter streamRecentChanges(String wiki, boolean includeBots, Long limit) {
        SseEmitter emitter = new SseEmitter(0L);
        AtomicReference<Future<?>> streamTask = new AtomicReference<>();
        AtomicReference<InputStream> responseBody = new AtomicReference<>();

        Runnable cleanup = () -> {
            Future<?> future = streamTask.get();
            if (future != null && !future.isDone()) {
                future.cancel(true);
            }
            closeQuietly(responseBody.get());
        };

        emitter.onCompletion(cleanup);
        emitter.onTimeout(() -> {
            cleanup.run();
            emitter.complete();
        });
        emitter.onError(error -> cleanup.run());

        Future<?> future = executorService.submit(() ->
                streamToEmitter(emitter, responseBody, wiki, includeBots, limit));
        streamTask.set(future);

        return emitter;
    }

    private void streamToEmitter(
            SseEmitter emitter,
            AtomicReference<InputStream> responseBody,
            String wiki,
            boolean includeBots,
            Long limit
    ) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(properties.recentChangeUrl()))
                    .header("Accept", MediaType.TEXT_EVENT_STREAM_VALUE)
                    .header("User-Agent", "spring-boot-streaming-example/0.1.0")
                    .GET()
                    .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            responseBody.set(response.body());

            long emitted = 0;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null && !Thread.currentThread().isInterrupted()) {
                    if (!line.startsWith("data: ")) {
                        continue;
                    }

                    RecentChange change = objectMapper.readValue(line.substring(6), RecentChange.class);
                    if (!matches(change, wiki, includeBots)) {
                        continue;
                    }

                    emitter.send(SseEmitter.event()
                            .data(change, MediaType.APPLICATION_JSON));

                    emitted++;
                    if (limit != null && emitted >= limit) {
                        break;
                    }
                }
            }

            emitter.complete();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException | RuntimeException e) {
            if (!Thread.currentThread().isInterrupted()) {
                emitter.completeWithError(e);
            }
        }
    }

    private boolean matches(RecentChange change, String wiki, boolean includeBots) {
        return (wiki == null || wiki.equalsIgnoreCase(change.getWiki()))
                && (includeBots || !Boolean.TRUE.equals(change.getBot()));
    }

    private void closeQuietly(InputStream inputStream) {
        if (inputStream == null) {
            return;
        }

        try {
            inputStream.close();
        } catch (IOException ignored) {
        }
    }
}
