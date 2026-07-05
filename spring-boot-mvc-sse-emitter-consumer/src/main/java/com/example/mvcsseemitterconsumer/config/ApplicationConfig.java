package com.example.mvcsseemitterconsumer.config;

import com.example.mvcsseemitterconsumer.application.port.in.StreamRecentChangesUseCase;
import com.example.mvcsseemitterconsumer.application.port.out.RecentChangeStreamPort;
import com.example.mvcsseemitterconsumer.application.service.StreamRecentChangesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    StreamRecentChangesUseCase streamRecentChangesUseCase(RecentChangeStreamPort recentChanges) {
        return new StreamRecentChangesService(recentChanges);
    }
}
