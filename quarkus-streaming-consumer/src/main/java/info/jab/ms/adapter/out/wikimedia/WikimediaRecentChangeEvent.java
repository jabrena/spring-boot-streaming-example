package info.jab.ms.adapter.out.wikimedia;

import info.jab.ms.domain.model.ChangeLength;
import info.jab.ms.domain.model.Meta;
import info.jab.ms.domain.model.RecentChange;
import info.jab.ms.domain.model.Revision;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WikimediaRecentChangeEvent(
        @JsonProperty("$schema")
        String schema,
        WikimediaMeta meta,
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
        WikimediaChangeLength length,
        WikimediaRevision revision,
        @JsonProperty("server_url")
        String serverUrl,
        @JsonProperty("server_name")
        String serverName,
        @JsonProperty("server_script_path")
        String serverScriptPath,
        String wiki,
        String parsedcomment
) {

    RecentChange toDomain() {
        Meta domainMeta = meta == null ? null : meta.toDomain();
        ChangeLength domainLength = length == null ? null : length.toDomain();
        Revision domainRevision = revision == null ? null : revision.toDomain();

        return new RecentChange(
                schema,
                domainMeta,
                id,
                type,
                namespace,
                title,
                titleUrl,
                comment,
                timestamp,
                user,
                bot,
                minor,
                domainLength,
                domainRevision,
                serverUrl,
                serverName,
                serverScriptPath,
                wiki,
                parsedcomment
        );
    }
}
