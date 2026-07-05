package info.jab.ms.application.port.in;

public record RecentChangeQuery(
        String wiki,
        Boolean includeBots,
        Long limit
) {
}
