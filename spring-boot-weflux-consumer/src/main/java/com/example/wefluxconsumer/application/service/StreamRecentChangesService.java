package com.example.wefluxconsumer.application.service;

import com.example.wefluxconsumer.application.port.in.RecentChangeQuery;
import com.example.wefluxconsumer.application.port.in.StreamRecentChangesUseCase;
import com.example.wefluxconsumer.application.port.out.RecentChangeStreamPort;
import com.example.wefluxconsumer.domain.model.RecentChange;
import reactor.core.publisher.Flux;

public class StreamRecentChangesService implements StreamRecentChangesUseCase {

    private final RecentChangeStreamPort recentChanges;

    public StreamRecentChangesService(RecentChangeStreamPort recentChanges) {
        this.recentChanges = recentChanges;
    }

    @Override
    public Flux<RecentChange> streamRecentChanges(RecentChangeQuery query) {
        Flux<RecentChange> stream = recentChanges.streamRecentChanges()
                .filter(change -> query.wiki() == null || query.wiki().equalsIgnoreCase(change.wiki()))
                .filter(change -> Boolean.TRUE.equals(query.includeBots()) || !Boolean.TRUE.equals(change.bot()));

        if (query.limit() != null) {
            stream = stream.take(query.limit());
        }

        return stream;
    }
}
