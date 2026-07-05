package com.example.wefluxconsumer.adapter.out.wikimedia;

import com.example.wefluxconsumer.domain.model.ChangeLength;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikimediaChangeLength(
        Integer old,
        @JsonProperty("new")
        Integer newValue
) {

    ChangeLength toDomain() {
        return new ChangeLength(old, newValue);
    }
}
