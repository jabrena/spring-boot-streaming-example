package info.jab.ms.adapter.out.wikimedia;

import info.jab.ms.application.port.out.RecentChangeStreamPort;
import info.jab.ms.config.WikimediaProperties;
import info.jab.ms.domain.model.RecentChange;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.BackPressureStrategy;
import io.smallrye.mutiny.subscription.MultiEmitter;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

@ApplicationScoped
public class WikimediaRecentChangeClient implements RecentChangeStreamPort {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final WikimediaProperties properties;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Inject
    public WikimediaRecentChangeClient(
            HttpClient wikimediaHttpClient,
            ObjectMapper objectMapper,
            WikimediaProperties properties
    ) {
        this.httpClient = wikimediaHttpClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public Multi<RecentChange> streamRecentChanges() {
        return Multi.createFrom().emitter(emitter -> {
            Future<?> task = executor.submit(() -> streamRecentChanges(emitter));
            emitter.onTermination(() -> task.cancel(true));
        }, BackPressureStrategy.BUFFER);
    }

    @PreDestroy
    void shutdown() {
        executor.shutdownNow();
    }

    private void streamRecentChanges(MultiEmitter<? super RecentChange> emitter) {
        HttpRequest request = HttpRequest.newBuilder(properties.recentChangeUri())
                .header("Accept", "text/event-stream")
                .header("User-Agent", "spring-boot-streaming-example/0.1.0")
                .GET()
                .build();

        try {
            HttpResponse<Stream<String>> response = httpClient.send(request, HttpResponse.BodyHandlers.ofLines());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                emitter.fail(new IllegalStateException(
                        "Wikimedia stream returned HTTP status " + response.statusCode()
                ));
                return;
            }

            try (Stream<String> lines = response.body()) {
                readSseLines(lines, emitter);
            }
            emitter.complete();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            emitter.complete();
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted()) {
                emitter.complete();
            } else {
                emitter.fail(e);
            }
        }
    }

    private void readSseLines(Stream<String> lines, MultiEmitter<? super RecentChange> emitter) throws IOException {
        StringBuilder data = new StringBuilder();
        Iterator<String> iterator = lines.iterator();

        while (!Thread.currentThread().isInterrupted() && iterator.hasNext()) {
            String line = iterator.next();
            if (line.isEmpty()) {
                emitData(data, emitter);
                data.setLength(0);
            } else if (line.startsWith("data:")) {
                if (!data.isEmpty()) {
                    data.append('\n');
                }
                data.append(line.substring("data:".length()).stripLeading());
            }
        }

        emitData(data, emitter);
    }

    private void emitData(StringBuilder data, MultiEmitter<? super RecentChange> emitter) throws IOException {
        if (data.isEmpty()) {
            return;
        }

        WikimediaRecentChangeEvent event = objectMapper.readValue(data.toString(), WikimediaRecentChangeEvent.class);
        emitter.emit(event.toDomain());
    }
}
