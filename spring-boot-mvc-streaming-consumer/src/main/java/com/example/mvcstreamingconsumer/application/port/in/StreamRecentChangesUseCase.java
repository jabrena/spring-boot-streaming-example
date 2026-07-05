package com.example.mvcstreamingconsumer.application.port.in;

import com.example.mvcstreamingconsumer.domain.model.RecentChange;

import java.util.stream.Stream;

public interface StreamRecentChangesUseCase {

    Stream<RecentChange> streamRecentChanges(RecentChangeQuery query);
}
