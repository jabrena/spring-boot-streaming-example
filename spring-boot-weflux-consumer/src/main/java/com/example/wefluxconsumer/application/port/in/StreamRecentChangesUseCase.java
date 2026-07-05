package com.example.wefluxconsumer.application.port.in;

import com.example.wefluxconsumer.domain.model.RecentChange;
import reactor.core.publisher.Flux;

public interface StreamRecentChangesUseCase {

    Flux<RecentChange> streamRecentChanges(RecentChangeQuery query);
}
