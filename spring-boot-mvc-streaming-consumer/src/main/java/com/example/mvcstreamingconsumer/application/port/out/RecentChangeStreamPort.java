package com.example.mvcstreamingconsumer.application.port.out;

import com.example.mvcstreamingconsumer.domain.model.RecentChange;

import java.util.stream.Stream;

public interface RecentChangeStreamPort {

    Stream<RecentChange> streamRecentChanges();
}
