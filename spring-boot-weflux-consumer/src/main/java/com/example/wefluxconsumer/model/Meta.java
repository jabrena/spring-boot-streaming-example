package com.example.wefluxconsumer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Meta(
        String uri,
        @JsonProperty("request_id")
        String requestId,
        String id,
        String domain,
        String stream,
        String dt
) {
}
