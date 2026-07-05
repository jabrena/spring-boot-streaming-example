package info.jab.ms.adapter.in.web;

import info.jab.ms.application.port.in.RecentChangeQuery;
import info.jab.ms.application.port.in.StreamRecentChangesUseCase;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.sse.Event;
import jakarta.inject.Inject;
import java.util.Optional;
import reactor.core.publisher.Flux;

@Controller("/api/wikipedia/recent-changes")
public class WikipediaController {

    private final StreamRecentChangesUseCase useCase;

    @Inject
    public WikipediaController(StreamRecentChangesUseCase useCase) {
        this.useCase = useCase;
    }

    @Get(produces = MediaType.TEXT_EVENT_STREAM)
    public MutableHttpResponse<Flux<Event<RecentChangeResponse>>> streamRecentChanges(
            @QueryValue Optional<String> wiki,
            @QueryValue(defaultValue = "false") Boolean includeBots,
            @QueryValue Optional<Long> limit
    ) {
        Flux<Event<RecentChangeResponse>> stream = useCase.streamRecentChanges(
                        new RecentChangeQuery(wiki.orElse(null), includeBots, limit.orElse(null)))
                .map(RecentChangeResponse::fromDomain)
                .map(Event::of);

        return HttpResponse.ok(stream)
                .contentType(MediaType.TEXT_EVENT_STREAM_TYPE)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header("X-Accel-Buffering", "no");
    }
}
