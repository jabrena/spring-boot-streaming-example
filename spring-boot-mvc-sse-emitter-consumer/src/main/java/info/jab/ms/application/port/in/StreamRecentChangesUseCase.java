package info.jab.ms.application.port.in;

import info.jab.ms.domain.model.RecentChange;

import java.util.stream.Stream;

public interface StreamRecentChangesUseCase {

    Stream<RecentChange> streamRecentChanges(RecentChangeQuery query);
}
