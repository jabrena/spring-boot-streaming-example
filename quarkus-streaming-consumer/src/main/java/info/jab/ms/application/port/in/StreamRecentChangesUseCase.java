package info.jab.ms.application.port.in;

import info.jab.ms.domain.model.RecentChange;
import io.smallrye.mutiny.Multi;

public interface StreamRecentChangesUseCase {

    Multi<RecentChange> streamRecentChanges(RecentChangeQuery query);
}
