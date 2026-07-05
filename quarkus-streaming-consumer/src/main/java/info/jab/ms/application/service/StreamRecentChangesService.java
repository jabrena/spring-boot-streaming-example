package info.jab.ms.application.service;

import info.jab.ms.application.port.in.RecentChangeQuery;
import info.jab.ms.application.port.in.StreamRecentChangesUseCase;
import info.jab.ms.application.port.out.RecentChangeStreamPort;
import info.jab.ms.domain.model.RecentChange;
import io.smallrye.mutiny.Multi;

public class StreamRecentChangesService implements StreamRecentChangesUseCase {

    private final RecentChangeStreamPort recentChanges;

    public StreamRecentChangesService(RecentChangeStreamPort recentChanges) {
        this.recentChanges = recentChanges;
    }

    @Override
    public Multi<RecentChange> streamRecentChanges(RecentChangeQuery query) {
        Multi<RecentChange> stream = recentChanges.streamRecentChanges()
                .select().where(change -> query.wiki() == null || query.wiki().equalsIgnoreCase(change.wiki()))
                .select().where(change -> Boolean.TRUE.equals(query.includeBots()) || !Boolean.TRUE.equals(change.bot()));

        if (query.limit() != null) {
            stream = stream.select().first(query.limit());
        }

        return stream;
    }
}
