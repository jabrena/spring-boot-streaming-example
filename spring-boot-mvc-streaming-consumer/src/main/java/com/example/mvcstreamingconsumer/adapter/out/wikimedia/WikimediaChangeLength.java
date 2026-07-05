package com.example.mvcstreamingconsumer.adapter.out.wikimedia;

import com.example.mvcstreamingconsumer.domain.model.ChangeLength;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
record WikimediaChangeLength(
        Integer old,
        @JsonProperty("new")
        Integer newValue
) {

    ChangeLength toDomain() {
        return new ChangeLength(old, newValue);
    }
}
