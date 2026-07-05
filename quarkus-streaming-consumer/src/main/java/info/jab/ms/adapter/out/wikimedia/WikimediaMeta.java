package info.jab.ms.adapter.out.wikimedia;

import info.jab.ms.domain.model.Meta;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikimediaMeta(
        String uri,
        @JsonProperty("request_id")
        String requestId,
        String id,
        String domain,
        String stream,
        String dt
) {

    Meta toDomain() {
        return new Meta(uri, requestId, id, domain, stream, dt);
    }
}
