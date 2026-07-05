package com.example.wefluxconsumer.adapter.out.wikimedia;

import com.example.wefluxconsumer.application.port.out.RecentChangeStreamPort;
import com.example.wefluxconsumer.config.WikimediaProperties;
import com.example.wefluxconsumer.domain.model.RecentChange;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class WikimediaRecentChangeClient implements RecentChangeStreamPort {

    private static final ParameterizedTypeReference<ServerSentEvent<WikimediaRecentChangeEvent>> RECENT_CHANGE_SSE =
            new ParameterizedTypeReference<>() {
            };

    private final WebClient webClient;
    private final WikimediaProperties properties;

    public WikimediaRecentChangeClient(WebClient wikimediaWebClient, WikimediaProperties properties) {
        this.webClient = wikimediaWebClient;
        this.properties = properties;
    }

    @Override
    public Flux<RecentChange> streamRecentChanges() {
        return webClient.get()
                .uri(properties.recentChangePath())
                .retrieve()
                .bodyToFlux(RECENT_CHANGE_SSE)
                .mapNotNull(ServerSentEvent::data)
                .map(WikimediaRecentChangeEvent::toDomain);
    }
}
