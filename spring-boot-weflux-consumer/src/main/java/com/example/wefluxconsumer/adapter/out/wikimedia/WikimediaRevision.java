package com.example.wefluxconsumer.adapter.out.wikimedia;

import com.example.wefluxconsumer.domain.model.Revision;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikimediaRevision(
        Long old,
        @JsonProperty("new")
        Long newValue
) {

    Revision toDomain() {
        return new Revision(old, newValue);
    }
}
