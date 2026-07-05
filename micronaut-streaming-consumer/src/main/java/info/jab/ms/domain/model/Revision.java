package info.jab.ms.domain.model;

public record Revision(
        Long old,
        Long newValue
) {
}
