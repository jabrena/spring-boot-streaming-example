package info.jab.ms.application.port.out;

import info.jab.ms.domain.model.RecentChange;

import java.util.stream.Stream;

public interface RecentChangeStreamPort {

    Stream<RecentChange> streamRecentChanges();
}
