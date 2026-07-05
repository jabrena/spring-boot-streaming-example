package com.example.wefluxconsumer.application.port.in;

public record RecentChangeQuery(
        String wiki,
        Boolean includeBots,
        Long limit
) {
}
