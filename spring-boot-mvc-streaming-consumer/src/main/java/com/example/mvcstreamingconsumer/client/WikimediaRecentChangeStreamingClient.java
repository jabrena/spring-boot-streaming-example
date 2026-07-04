package com.example.mvcstreamingconsumer.client;

import com.example.mvcstreamingconsumer.config.WikimediaProperties;
import com.example.mvcstreamingconsumer.model.RecentChange;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class WikimediaRecentChangeStreamingClient {

    private final WikimediaProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public WikimediaRecentChangeStreamingClient(WikimediaProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public StreamingResponseBody streamRecentChanges(String wiki, boolean includeBots, Long limit) {
        return outputStream -> {
            HttpRequest request = HttpRequest.newBuilder(URI.create(properties.recentChangeUrl()))
                    .header("Accept", MediaType.TEXT_EVENT_STREAM_VALUE)
                    .header("User-Agent", "spring-boot-streaming-example/0.0.1")
                    .GET()
                    .build();

            HttpResponse<java.io.InputStream> response;
            try {
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while connecting to Wikimedia stream", e);
            }

            long[] emitted = {0};

            try (BufferedReader reader = new BufferedReader(
                    new java.io.InputStreamReader(response.body(), StandardCharsets.UTF_8));
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null && !Thread.currentThread().isInterrupted()) {
                    if (!line.startsWith("data: ")) {
                        continue;
                    }

                    RecentChange change = objectMapper.readValue(line.substring(6), RecentChange.class);
                    if (!matches(change, wiki, includeBots)) {
                        continue;
                    }

                    writer.print("data:");
                    writer.print(objectMapper.writeValueAsString(change));
                    writer.print("\n\n");
                    writer.flush();

                    emitted[0]++;
                    if (limit != null && emitted[0] >= limit) {
                        break;
                    }
                }
            }
        };
    }

    private boolean matches(RecentChange change, String wiki, boolean includeBots) {
        return (wiki == null || wiki.equalsIgnoreCase(change.wiki()))
                && (includeBots || !Boolean.TRUE.equals(change.bot()));
    }
}
