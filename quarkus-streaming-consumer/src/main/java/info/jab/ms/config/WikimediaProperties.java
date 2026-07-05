package info.jab.ms.config;

import io.smallrye.config.ConfigMapping;
import java.net.URI;

@ConfigMapping(prefix = "wikimedia")
public interface WikimediaProperties {

    URI baseUrl();

    String recentChangePath();

    default URI recentChangeUri() {
        return baseUrl().resolve(recentChangePath());
    }
}
