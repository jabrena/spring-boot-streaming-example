package com.example.mvcstreamingconsumer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChangeLength(
        Integer old,
        @JsonProperty("new")
        Integer newValue
) {
}
