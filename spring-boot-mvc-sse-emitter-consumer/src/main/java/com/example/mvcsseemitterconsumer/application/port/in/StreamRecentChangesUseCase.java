package com.example.mvcsseemitterconsumer.application.port.in;

import com.example.mvcsseemitterconsumer.domain.model.RecentChange;

import java.util.stream.Stream;

public interface StreamRecentChangesUseCase {

    Stream<RecentChange> streamRecentChanges(RecentChangeQuery query);
}
