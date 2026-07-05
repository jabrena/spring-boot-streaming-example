package info.jab.ms.adapter.out.wikimedia;

import info.jab.ms.application.port.out.RecentChangeStreamPort;
import info.jab.ms.config.WikimediaProperties;
import info.jab.ms.domain.model.RecentChange;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class WikimediaRecentChangeStreamingClient implements RecentChangeStreamPort {

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

    @Override
    public Stream<RecentChange> streamRecentChanges() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(properties.recentChangeUrl()))
                .header("Accept", MediaType.TEXT_EVENT_STREAM_VALUE)
                .header("User-Agent", "spring-boot-streaming-example/0.1.0")
                .GET()
                .build();

        HttpResponse<java.io.InputStream> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to connect to Wikimedia stream", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while connecting to Wikimedia stream", e);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8));
        var iterator = new RecentChangeEventIterator(reader, objectMapper);

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false)
                .onClose(iterator::close);
    }
}
