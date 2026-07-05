package com.example.mvcsseemitterconsumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "streaming.cors")
public record StreamingCorsProperties(
        String[] allowedOrigins
) {
}
