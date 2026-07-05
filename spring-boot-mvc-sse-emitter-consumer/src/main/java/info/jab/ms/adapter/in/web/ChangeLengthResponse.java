package info.jab.ms.adapter.in.web;

import info.jab.ms.domain.model.ChangeLength;
import com.fasterxml.jackson.annotation.JsonProperty;

record ChangeLengthResponse(
        Integer old,
        @JsonProperty("new")
        Integer newValue
) {

    static ChangeLengthResponse fromDomain(ChangeLength length) {
        return length == null ? null : new ChangeLengthResponse(length.old(), length.newValue());
    }
}
