package info.jab.ms.config;

import info.jab.ms.application.port.in.StreamRecentChangesUseCase;
import info.jab.ms.application.port.out.RecentChangeStreamPort;
import info.jab.ms.application.service.StreamRecentChangesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    StreamRecentChangesUseCase streamRecentChangesUseCase(RecentChangeStreamPort recentChanges) {
        return new StreamRecentChangesService(recentChanges);
    }
}
