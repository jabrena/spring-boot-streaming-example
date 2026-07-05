package info.jab.ms.config;

import info.jab.ms.application.port.in.StreamRecentChangesUseCase;
import info.jab.ms.application.port.out.RecentChangeStreamPort;
import info.jab.ms.application.service.StreamRecentChangesService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.net.http.HttpClient;
import java.time.Duration;

@ApplicationScoped
public class ApplicationConfig {

    @Produces
    StreamRecentChangesUseCase streamRecentChangesUseCase(RecentChangeStreamPort recentChanges) {
        return new StreamRecentChangesService(recentChanges);
    }

    @Produces
    HttpClient wikimediaHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }
}
