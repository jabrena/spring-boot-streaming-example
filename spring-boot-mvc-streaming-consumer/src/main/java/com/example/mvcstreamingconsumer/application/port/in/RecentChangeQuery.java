package com.example.mvcstreamingconsumer.application.port.in;

public record RecentChangeQuery(
        String wiki,
        Boolean includeBots,
        Long limit
) {
}
