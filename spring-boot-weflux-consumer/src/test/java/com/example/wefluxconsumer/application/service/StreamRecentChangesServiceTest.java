package com.example.wefluxconsumer.application.service;

import com.example.wefluxconsumer.application.port.in.RecentChangeQuery;
import com.example.wefluxconsumer.domain.model.RecentChange;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class StreamRecentChangesServiceTest {

    @Test
    void should_filter_by_wiki_and_exclude_bots_by_default() {
        StreamRecentChangesService service = new StreamRecentChangesService(() -> Flux.just(
                recentChange(1L, "enwiki", false),
                recentChange(2L, "enwiki", true),
                recentChange(3L, "dewiki", false)
        ));

        StepVerifier.create(service.streamRecentChanges(new RecentChangeQuery("enwiki", false, null))
                        .map(RecentChange::id))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void should_include_bots_and_apply_limit_when_requested() {
        StreamRecentChangesService service = new StreamRecentChangesService(() -> Flux.just(
                recentChange(1L, "enwiki", false),
                recentChange(2L, "enwiki", true),
                recentChange(3L, "dewiki", false)
        ));

        StepVerifier.create(service.streamRecentChanges(new RecentChangeQuery(null, true, 2L))
                        .map(RecentChange::id))
                .expectNext(1L, 2L)
                .verifyComplete();
    }

    private static RecentChange recentChange(Long id, String wiki, Boolean bot) {
        return new RecentChange(
                null,
                null,
                id,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                bot,
                null,
                null,
                null,
                null,
                null,
                null,
                wiki,
                null
        );
    }
}
