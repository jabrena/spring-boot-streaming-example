package info.jab.ms.adapter.out.wikimedia;

import info.jab.ms.application.port.out.RecentChangeStreamPort;
import info.jab.ms.config.WikimediaProperties;
import info.jab.ms.domain.model.RecentChange;
import io.micronaut.json.JsonMapper;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Singleton
public class WikimediaRecentChangeClient implements RecentChangeStreamPort {

    private final HttpClient httpClient;
    private final JsonMapper jsonMapper;
    private final WikimediaProperties properties;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Inject
    public WikimediaRecentChangeClient(
            HttpClient wikimediaHttpClient,
            JsonMapper jsonMapper,
            WikimediaProperties properties
    ) {
        this.httpClient = wikimediaHttpClient;
        this.jsonMapper = jsonMapper;
        this.properties = properties;
    }

    @Override
    public Flux<RecentChange> streamRecentChanges() {
        return Flux.create(sink -> {
            Future<?> task = executor.submit(() -> streamRecentChanges(sink));
            sink.onDispose(() -> task.cancel(true));
        }, FluxSink.OverflowStrategy.BUFFER);
    }

    @PreDestroy
    void shutdown() {
        executor.shutdownNow();
    }

    private void streamRecentChanges(FluxSink<? super RecentChange> sink) {
        HttpRequest request = HttpRequest.newBuilder(properties.recentChangeUri())
                .header("Accept", "text/event-stream")
                .header("User-Agent", "multi-framework-streaming-example/0.1.0")
                .GET()
                .build();

        try {
            HttpResponse<Stream<String>> response = httpClient.send(request, HttpResponse.BodyHandlers.ofLines());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                sink.error(new IllegalStateException(
                        "Wikimedia stream returned HTTP status " + response.statusCode()
                ));
                return;
            }

            try (Stream<String> lines = response.body()) {
                readSseLines(lines, sink);
            }
            sink.complete();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            sink.complete();
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted()) {
                sink.complete();
            } else {
                sink.error(e);
            }
        }
    }

    private void readSseLines(Stream<String> lines, FluxSink<? super RecentChange> sink) throws IOException {
        StringBuilder data = new StringBuilder();
        Iterator<String> iterator = lines.iterator();

        while (!Thread.currentThread().isInterrupted() && !sink.isCancelled() && iterator.hasNext()) {
            String line = iterator.next();
            if (line.isEmpty()) {
                emitData(data, sink);
                data.setLength(0);
            } else if (line.startsWith("data:")) {
                if (!data.isEmpty()) {
                    data.append('\n');
                }
                data.append(line.substring("data:".length()).stripLeading());
            }
        }

        emitData(data, sink);
    }

    private void emitData(StringBuilder data, FluxSink<? super RecentChange> sink) throws IOException {
        if (data.isEmpty() || sink.isCancelled()) {
            return;
        }

        WikimediaRecentChangeEvent event = jsonMapper.readValue(data.toString(), WikimediaRecentChangeEvent.class);
        sink.next(event.toDomain());
    }
}
