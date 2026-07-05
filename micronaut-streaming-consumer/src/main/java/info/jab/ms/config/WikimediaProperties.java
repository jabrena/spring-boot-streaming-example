package info.jab.ms.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import java.net.URI;

@ConfigurationProperties("wikimedia")
public class WikimediaProperties {

    private URI baseUrl;
    private String recentChangePath;

    public URI getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URI baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getRecentChangePath() {
        return recentChangePath;
    }

    public void setRecentChangePath(String recentChangePath) {
        this.recentChangePath = recentChangePath;
    }

    public URI recentChangeUri() {
        return baseUrl.resolve(recentChangePath);
    }
}
