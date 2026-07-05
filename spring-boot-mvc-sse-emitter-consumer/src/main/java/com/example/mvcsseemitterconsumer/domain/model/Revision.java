package com.example.mvcsseemitterconsumer.domain.model;

public record Revision(
        Long old,
        Long newValue
) {
}
