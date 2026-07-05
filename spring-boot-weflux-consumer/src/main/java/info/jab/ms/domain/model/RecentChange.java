package info.jab.ms.domain.model;

public record RecentChange(
        String schema,
        Meta meta,
        Long id,
        String type,
        Integer namespace,
        String title,
        String titleUrl,
        String comment,
        Long timestamp,
        String user,
        Boolean bot,
        Boolean minor,
        ChangeLength length,
        Revision revision,
        String serverUrl,
        String serverName,
        String serverScriptPath,
        String wiki,
        String parsedcomment
) {
}
