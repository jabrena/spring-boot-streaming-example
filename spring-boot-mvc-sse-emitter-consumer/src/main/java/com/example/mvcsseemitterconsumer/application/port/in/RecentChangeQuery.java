package com.example.mvcsseemitterconsumer.application.port.in;

public record RecentChangeQuery(
        String wiki,
        Boolean includeBots,
        Long limit
) {
}
