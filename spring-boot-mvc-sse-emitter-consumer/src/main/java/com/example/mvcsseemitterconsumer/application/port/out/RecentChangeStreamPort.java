package com.example.mvcsseemitterconsumer.application.port.out;

import com.example.mvcsseemitterconsumer.domain.model.RecentChange;

import java.util.stream.Stream;

public interface RecentChangeStreamPort {

    Stream<RecentChange> streamRecentChanges();
}
