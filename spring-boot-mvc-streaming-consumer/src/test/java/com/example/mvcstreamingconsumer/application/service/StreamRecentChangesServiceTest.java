package com.example.mvcstreamingconsumer.application.service;

import com.example.mvcstreamingconsumer.application.port.in.RecentChangeQuery;
import com.example.mvcstreamingconsumer.domain.model.RecentChange;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StreamRecentChangesServiceTest {

    @Test
    void should_filter_by_wiki_and_exclude_bots_by_default() {
        StreamRecentChangesService service = new StreamRecentChangesService(() -> Stream.of(
                recentChange(1L, "enwiki", false),
                recentChange(2L, "enwiki", true),
                recentChange(3L, "dewiki", false)
        ));

        List<Long> ids = service.streamRecentChanges(new RecentChangeQuery("enwiki", false, null))
                .map(RecentChange::id)
                .toList();

        assertThat(ids).containsExactly(1L);
    }

    @Test
    void should_include_bots_and_apply_limit_when_requested() {
        StreamRecentChangesService service = new StreamRecentChangesService(() -> Stream.of(
                recentChange(1L, "enwiki", false),
                recentChange(2L, "enwiki", true),
                recentChange(3L, "dewiki", false)
        ));

        List<Long> ids = service.streamRecentChanges(new RecentChangeQuery(null, true, 2L))
                .map(RecentChange::id)
                .toList();

        assertThat(ids).containsExactly(1L, 2L);
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
