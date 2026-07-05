package info.jab.ms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wikimedia")
public record WikimediaProperties(
        String recentChangeUrl
) {
}
