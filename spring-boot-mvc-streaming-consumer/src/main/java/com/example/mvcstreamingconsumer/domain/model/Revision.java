package com.example.mvcstreamingconsumer.domain.model;

public record Revision(
        Long old,
        Long newValue
) {
}
