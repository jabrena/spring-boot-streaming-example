package com.example.wefluxconsumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "streaming.cors")
public record StreamingCorsProperties(
        String[] allowedOrigins
) {
}
