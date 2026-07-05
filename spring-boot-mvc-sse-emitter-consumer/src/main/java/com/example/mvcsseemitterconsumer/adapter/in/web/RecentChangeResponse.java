package com.example.mvcsseemitterconsumer.adapter.in.web;

import com.example.mvcsseemitterconsumer.domain.model.RecentChange;
import com.fasterxml.jackson.annotation.JsonProperty;

record RecentChangeResponse(
        @JsonProperty("$schema")
        String schema,
        MetaResponse meta,
        Long id,
        String type,
        Integer namespace,
        String title,
        @JsonProperty("title_url")
        String titleUrl,
        String comment,
        Long timestamp,
        String user,
        Boolean bot,
        Boolean minor,
        ChangeLengthResponse length,
        RevisionResponse revision,
        @JsonProperty("server_url")
        String serverUrl,
        @JsonProperty("server_name")
        String serverName,
        @JsonProperty("server_script_path")
        String serverScriptPath,
        String wiki,
        String parsedcomment
) {

    static RecentChangeResponse fromDomain(RecentChange change) {
        return new RecentChangeResponse(
                change.schema(),
                MetaResponse.fromDomain(change.meta()),
                change.id(),
                change.type(),
                change.namespace(),
                change.title(),
                change.titleUrl(),
                change.comment(),
                change.timestamp(),
                change.user(),
                change.bot(),
                change.minor(),
                ChangeLengthResponse.fromDomain(change.length()),
                RevisionResponse.fromDomain(change.revision()),
                change.serverUrl(),
                change.serverName(),
                change.serverScriptPath(),
                change.wiki(),
                change.parsedcomment()
        );
    }
}
