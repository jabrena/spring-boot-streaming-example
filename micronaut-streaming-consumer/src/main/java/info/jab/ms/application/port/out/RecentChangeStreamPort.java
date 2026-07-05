package info.jab.ms.application.port.out;

import info.jab.ms.domain.model.RecentChange;
import reactor.core.publisher.Flux;

public interface RecentChangeStreamPort {

    Flux<RecentChange> streamRecentChanges();
}
