package com.example.mvcstreamingconsumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wikimedia")
public record WikimediaProperties(
        String recentChangeUrl
) {
}
