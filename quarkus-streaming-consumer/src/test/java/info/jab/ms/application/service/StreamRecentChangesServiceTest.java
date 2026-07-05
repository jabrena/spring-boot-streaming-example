package info.jab.ms.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import info.jab.ms.application.port.in.RecentChangeQuery;
import info.jab.ms.domain.model.RecentChange;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.test.AssertSubscriber;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;

class StreamRecentChangesServiceTest {

    @Test
    void should_filter_by_wiki_and_exclude_bots_by_default() {
        StreamRecentChangesService service = new StreamRecentChangesService(() -> Multi.createFrom().items(
                recentChange(1L, "enwiki", false),
                recentChange(2L, "enwiki", true),
                recentChange(3L, "dewiki", false)
        ));

        List<Long> ids = service.streamRecentChanges(new RecentChangeQuery("enwiki", false, null))
                .map(RecentChange::id)
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .awaitCompletion(Duration.ofSeconds(1))
                .getItems();

        assertEquals(List.of(1L), ids);
    }

    @Test
    void should_include_bots_and_apply_limit_when_requested() {
        StreamRecentChangesService service = new StreamRecentChangesService(() -> Multi.createFrom().items(
                recentChange(1L, "enwiki", false),
                recentChange(2L, "enwiki", true),
                recentChange(3L, "dewiki", false)
        ));

        List<Long> ids = service.streamRecentChanges(new RecentChangeQuery(null, true, 2L))
                .map(RecentChange::id)
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .awaitCompletion(Duration.ofSeconds(1))
                .getItems();

        assertEquals(List.of(1L, 2L), ids);
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
