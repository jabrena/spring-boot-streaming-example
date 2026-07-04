package com.example.wefluxconsumer.client;

import com.example.wefluxconsumer.config.WikimediaProperties;
import com.example.wefluxconsumer.model.RecentChange;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class WikimediaRecentChangeClient {

    private static final ParameterizedTypeReference<ServerSentEvent<RecentChange>> RECENT_CHANGE_SSE =
            new ParameterizedTypeReference<>() {
            };

    private final WebClient webClient;
    private final WikimediaProperties properties;

    public WikimediaRecentChangeClient(WebClient wikimediaWebClient, WikimediaProperties properties) {
        this.webClient = wikimediaWebClient;
        this.properties = properties;
    }

    public Flux<RecentChange> streamRecentChanges() {
        return webClient.get()
                .uri(properties.recentChangePath())
                .retrieve()
                .bodyToFlux(RECENT_CHANGE_SSE)
                .mapNotNull(ServerSentEvent::data);
    }
}
