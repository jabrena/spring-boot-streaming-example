package info.jab.ms.config;

import info.jab.ms.application.port.in.StreamRecentChangesUseCase;
import info.jab.ms.application.port.out.RecentChangeStreamPort;
import info.jab.ms.application.service.StreamRecentChangesService;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import java.net.http.HttpClient;
import java.time.Duration;

@Factory
public class ApplicationConfig {

    @Bean
    @Singleton
    StreamRecentChangesUseCase streamRecentChangesUseCase(RecentChangeStreamPort recentChanges) {
        return new StreamRecentChangesService(recentChanges);
    }

    @Bean
    @Singleton
    HttpClient wikimediaHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }
}
