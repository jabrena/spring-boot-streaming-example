package com.example.wefluxconsumer.application.port.out;

import com.example.wefluxconsumer.domain.model.RecentChange;
import reactor.core.publisher.Flux;

public interface RecentChangeStreamPort {

    Flux<RecentChange> streamRecentChanges();
}
