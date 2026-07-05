package info.jab.ms.adapter.in.web;

import info.jab.ms.api.WikipediaApi;
import info.jab.ms.application.port.in.RecentChangeQuery;
import info.jab.ms.application.port.in.StreamRecentChangesUseCase;
import info.jab.ms.domain.model.RecentChange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@RestController
public class WikipediaController implements WikipediaApi {

    private final StreamRecentChangesUseCase useCase;
    private final ExecutorService executorService;

    public WikipediaController(StreamRecentChangesUseCase useCase, ExecutorService executorService) {
        this.useCase = useCase;
        this.executorService = executorService;
    }

    @Override
    public ResponseEntity<SseEmitter> streamRecentChanges(
            String wiki,
            Boolean includeBots,
            Long limit
    ) {
        SseEmitter emitter = new SseEmitter(0L);
        AtomicReference<Future<?>> streamTask = new AtomicReference<>();
        AtomicReference<Stream<RecentChange>> activeStream = new AtomicReference<>();

        Runnable cleanup = () -> {
            Future<?> future = streamTask.get();
            if (future != null && !future.isDone()) {
                future.cancel(true);
            }
            closeQuietly(activeStream.get());
        };

        emitter.onCompletion(cleanup);
        emitter.onTimeout(() -> {
            cleanup.run();
            emitter.complete();
        });
        emitter.onError(error -> cleanup.run());

        Future<?> future = executorService.submit(() ->
                streamToEmitter(emitter, activeStream, new RecentChangeQuery(wiki, includeBots, limit)));
        streamTask.set(future);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header("X-Accel-Buffering", "no")
                .body(emitter);
    }

    private void streamToEmitter(
            SseEmitter emitter,
            AtomicReference<Stream<RecentChange>> activeStream,
            RecentChangeQuery query
    ) {
        try (Stream<RecentChange> changes = useCase.streamRecentChanges(query)) {
            activeStream.set(changes);

            var iterator = changes.iterator();
            while (iterator.hasNext() && !Thread.currentThread().isInterrupted()) {
                RecentChange change = iterator.next();
                emitter.send(SseEmitter.event()
                        .data(RecentChangeResponse.fromDomain(change), MediaType.APPLICATION_JSON));
            }

            emitter.complete();
        } catch (IOException | RuntimeException e) {
            if (!Thread.currentThread().isInterrupted()) {
                emitter.completeWithError(e);
            }
        }
    }

    private void closeQuietly(Stream<RecentChange> stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (RuntimeException ignored) {
            }
        }
    }
}
