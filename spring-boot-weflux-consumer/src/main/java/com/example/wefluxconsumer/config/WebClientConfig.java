package com.example.wefluxconsumer.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties({WikimediaProperties.class, StreamingCorsProperties.class})
public class WebClientConfig {

    @Bean
    WebClient wikimediaWebClient(WikimediaProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .build();
    }

    @Bean
    WebFluxConfigurer streamingCorsConfigurer(StreamingCorsProperties corsProperties) {
        return new WebFluxConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/wikipedia/recent-changes")
                        .allowedOrigins(corsProperties.allowedOrigins())
                        .allowedMethods("GET");
            }
        };
    }
}
