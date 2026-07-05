package com.example.wefluxconsumer.adapter.in.web;

import com.example.wefluxconsumer.domain.model.Revision;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RevisionResponse(
        Long old,
        @JsonProperty("new")
        Long newValue
) {

    static RevisionResponse fromDomain(Revision revision) {
        return revision == null ? null : new RevisionResponse(revision.old(), revision.newValue());
    }
}
