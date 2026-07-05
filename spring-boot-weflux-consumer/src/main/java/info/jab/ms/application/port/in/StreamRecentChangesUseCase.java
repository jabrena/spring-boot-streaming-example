package info.jab.ms.application.port.in;

import info.jab.ms.domain.model.RecentChange;
import reactor.core.publisher.Flux;

public interface StreamRecentChangesUseCase {

    Flux<RecentChange> streamRecentChanges(RecentChangeQuery query);
}
