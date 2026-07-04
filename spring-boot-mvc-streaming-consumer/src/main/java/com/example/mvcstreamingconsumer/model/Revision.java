package com.example.mvcstreamingconsumer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Revision(
        Long old,
        @JsonProperty("new")
        Long newValue
) {
}
