package com.example.mvcstreamingconsumer.adapter.in.web;

import com.example.mvcstreamingconsumer.api.WikipediaApi;
import com.example.mvcstreamingconsumer.application.port.in.RecentChangeQuery;
import com.example.mvcstreamingconsumer.application.port.in.StreamRecentChangesUseCase;
import com.example.mvcstreamingconsumer.domain.model.RecentChange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import tools.jackson.databind.ObjectMapper;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@RestController
public class WikipediaController implements WikipediaApi {

    private final StreamRecentChangesUseCase useCase;
    private final ObjectMapper objectMapper;

    public WikipediaController(StreamRecentChangesUseCase useCase, ObjectMapper objectMapper) {
        this.useCase = useCase;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseEntity<StreamingResponseBody> streamRecentChanges(
            String wiki,
            Boolean includeBots,
            Long limit
    ) {
        StreamingResponseBody responseBody = outputStream -> {
            try (Stream<RecentChange> changes = useCase.streamRecentChanges(new RecentChangeQuery(wiki, includeBots, limit));
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {

                var iterator = changes.iterator();
                while (iterator.hasNext() && !Thread.currentThread().isInterrupted()) {
                    RecentChange change = iterator.next();

                    writer.print("data:");
                    writer.print(objectMapper.writeValueAsString(RecentChangeResponse.fromDomain(change)));
                    writer.print("\n\n");
                    writer.flush();
                }
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header("X-Accel-Buffering", "no")
                .body(responseBody);
    }
}
