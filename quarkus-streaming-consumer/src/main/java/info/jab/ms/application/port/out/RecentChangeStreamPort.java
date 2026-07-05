package info.jab.ms.application.port.out;

import info.jab.ms.domain.model.RecentChange;
import io.smallrye.mutiny.Multi;

public interface RecentChangeStreamPort {

    Multi<RecentChange> streamRecentChanges();
}
