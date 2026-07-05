package com.example.wefluxconsumer.adapter.in.web;

import com.example.wefluxconsumer.api.WikipediaApi;
import com.example.wefluxconsumer.application.port.in.RecentChangeQuery;
import com.example.wefluxconsumer.application.port.in.StreamRecentChangesUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class WikipediaController implements WikipediaApi {

    private final StreamRecentChangesUseCase useCase;

    public WikipediaController(StreamRecentChangesUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public Mono<ResponseEntity<?>> streamRecentChanges(
            String wiki,
            Boolean includeBots,
            Long limit
    ) {
        Flux<RecentChangeResponse> stream = useCase.streamRecentChanges(new RecentChangeQuery(wiki, includeBots, limit))
                .map(RecentChangeResponse::fromDomain);

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header("X-Accel-Buffering", "no")
                .body(stream));
    }
}
