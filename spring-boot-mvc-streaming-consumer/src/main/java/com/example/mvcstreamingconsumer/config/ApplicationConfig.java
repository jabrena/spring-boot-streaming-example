package com.example.mvcstreamingconsumer.config;

import com.example.mvcstreamingconsumer.application.port.in.StreamRecentChangesUseCase;
import com.example.mvcstreamingconsumer.application.port.out.RecentChangeStreamPort;
import com.example.mvcstreamingconsumer.application.service.StreamRecentChangesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    StreamRecentChangesUseCase streamRecentChangesUseCase(RecentChangeStreamPort recentChanges) {
        return new StreamRecentChangesService(recentChanges);
    }
}
