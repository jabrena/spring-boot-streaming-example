package com.example.wefluxconsumer.config;

import com.example.wefluxconsumer.application.port.in.StreamRecentChangesUseCase;
import com.example.wefluxconsumer.application.port.out.RecentChangeStreamPort;
import com.example.wefluxconsumer.application.service.StreamRecentChangesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    StreamRecentChangesUseCase streamRecentChangesUseCase(RecentChangeStreamPort recentChanges) {
        return new StreamRecentChangesService(recentChanges);
    }
}
