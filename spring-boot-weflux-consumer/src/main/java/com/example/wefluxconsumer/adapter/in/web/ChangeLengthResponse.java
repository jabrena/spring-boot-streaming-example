package com.example.wefluxconsumer.adapter.in.web;

import com.example.wefluxconsumer.domain.model.ChangeLength;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ChangeLengthResponse(
        Integer old,
        @JsonProperty("new")
        Integer newValue
) {

    static ChangeLengthResponse fromDomain(ChangeLength length) {
        return length == null ? null : new ChangeLengthResponse(length.old(), length.newValue());
    }
}
