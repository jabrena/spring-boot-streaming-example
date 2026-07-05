package com.example.mvcsseemitterconsumer.adapter.out.wikimedia;

import com.example.mvcsseemitterconsumer.domain.model.Revision;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
record WikimediaRevision(
        Long old,
        @JsonProperty("new")
        Long newValue
) {

    Revision toDomain() {
        return new Revision(old, newValue);
    }
}
