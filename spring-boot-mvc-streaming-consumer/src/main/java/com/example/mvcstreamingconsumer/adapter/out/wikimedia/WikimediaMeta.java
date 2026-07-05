package com.example.mvcstreamingconsumer.adapter.out.wikimedia;

import com.example.mvcstreamingconsumer.domain.model.Meta;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
record WikimediaMeta(
        String uri,
        @JsonProperty("request_id")
        String requestId,
        String id,
        String domain,
        String stream,
        String dt
) {

    Meta toDomain() {
        return new Meta(uri, requestId, id, domain, stream, dt);
    }
}
