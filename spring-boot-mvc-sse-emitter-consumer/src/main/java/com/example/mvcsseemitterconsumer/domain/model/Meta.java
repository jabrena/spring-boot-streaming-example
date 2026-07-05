package com.example.mvcsseemitterconsumer.domain.model;

public record Meta(
        String uri,
        String requestId,
        String id,
        String domain,
        String stream,
        String dt
) {
}
