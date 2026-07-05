package com.example.mvcstreamingconsumer.adapter.in.web;

import com.example.mvcstreamingconsumer.domain.model.Meta;
import com.fasterxml.jackson.annotation.JsonProperty;

record MetaResponse(
        String uri,
        @JsonProperty("request_id")
        String requestId,
        String id,
        String domain,
        String stream,
        String dt
) {

    static MetaResponse fromDomain(Meta meta) {
        return meta == null ? null : new MetaResponse(
                meta.uri(),
                meta.requestId(),
                meta.id(),
                meta.domain(),
                meta.stream(),
                meta.dt()
        );
    }
}
