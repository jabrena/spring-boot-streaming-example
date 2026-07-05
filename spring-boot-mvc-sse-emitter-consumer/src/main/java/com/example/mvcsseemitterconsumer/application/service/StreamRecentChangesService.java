package com.example.mvcsseemitterconsumer.application.service;

import com.example.mvcsseemitterconsumer.application.port.in.RecentChangeQuery;
import com.example.mvcsseemitterconsumer.application.port.in.StreamRecentChangesUseCase;
import com.example.mvcsseemitterconsumer.application.port.out.RecentChangeStreamPort;
import com.example.mvcsseemitterconsumer.domain.model.RecentChange;

import java.util.stream.Stream;

public class StreamRecentChangesService implements StreamRecentChangesUseCase {

    private final RecentChangeStreamPort recentChanges;

    public StreamRecentChangesService(RecentChangeStreamPort recentChanges) {
        this.recentChanges = recentChanges;
    }

    @Override
    public Stream<RecentChange> streamRecentChanges(RecentChangeQuery query) {
        Stream<RecentChange> stream = recentChanges.streamRecentChanges()
                .filter(change -> query.wiki() == null || query.wiki().equalsIgnoreCase(change.wiki()))
                .filter(change -> Boolean.TRUE.equals(query.includeBots()) || !Boolean.TRUE.equals(change.bot()));

        if (query.limit() != null) {
            stream = stream.limit(query.limit());
        }

        return stream;
    }
}
