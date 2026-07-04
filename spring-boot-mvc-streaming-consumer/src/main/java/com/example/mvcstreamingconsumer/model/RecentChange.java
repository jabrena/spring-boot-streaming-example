package com.example.mvcstreamingconsumer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecentChange(
        @JsonProperty("$schema")
        String schema,
        Meta meta,
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
        ChangeLength length,
        Revision revision,
        @JsonProperty("server_url")
        String serverUrl,
        @JsonProperty("server_name")
        String serverName,
        @JsonProperty("server_script_path")
        String serverScriptPath,
        String wiki,
        String parsedcomment
) {
}
