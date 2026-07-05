package info.jab.ms.adapter.in.web;

import info.jab.ms.domain.model.Revision;
import com.fasterxml.jackson.annotation.JsonProperty;

record RevisionResponse(
        Long old,
        @JsonProperty("new")
        Long newValue
) {

    static RevisionResponse fromDomain(Revision revision) {
        return revision == null ? null : new RevisionResponse(revision.old(), revision.newValue());
    }
}
