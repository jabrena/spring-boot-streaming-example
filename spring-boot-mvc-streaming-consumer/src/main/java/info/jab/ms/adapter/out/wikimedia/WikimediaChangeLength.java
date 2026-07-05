package info.jab.ms.adapter.out.wikimedia;

import info.jab.ms.domain.model.ChangeLength;
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
