package com.example.wefluxconsumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wikimedia")
public record WikimediaProperties(
        String baseUrl,
        String recentChangePath
) {
}
