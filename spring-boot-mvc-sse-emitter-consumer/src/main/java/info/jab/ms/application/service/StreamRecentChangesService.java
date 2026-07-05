package info.jab.ms.application.service;

import info.jab.ms.application.port.in.RecentChangeQuery;
import info.jab.ms.application.port.in.StreamRecentChangesUseCase;
import info.jab.ms.application.port.out.RecentChangeStreamPort;
import info.jab.ms.domain.model.RecentChange;

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
