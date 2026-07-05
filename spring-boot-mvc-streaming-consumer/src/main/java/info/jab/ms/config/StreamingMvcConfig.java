package info.jab.ms.config;

import org.apache.coyote.ProtocolHandler;
import org.springframework.boot.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.ExecutorService;

@Configuration
@EnableConfigurationProperties({WikimediaProperties.class, StreamingCorsProperties.class})
public class StreamingMvcConfig {

    @Bean(destroyMethod = "close")
    ExecutorService mvcVirtualThreadExecutor() {
        return java.util.concurrent.Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("mvc-virtual-", 0).factory());
    }

    @Bean
    AsyncTaskExecutor streamingTaskExecutor(ExecutorService mvcVirtualThreadExecutor) {
        return new TaskExecutorAdapter(mvcVirtualThreadExecutor);
    }

    @Bean
    TomcatProtocolHandlerCustomizer<ProtocolHandler> virtualThreadTomcatCustomizer(
            ExecutorService mvcVirtualThreadExecutor
    ) {
        return protocolHandler -> protocolHandler.setExecutor(mvcVirtualThreadExecutor);
    }

    @Bean
    WebMvcConfigurer streamingMvcConfigurer(
            AsyncTaskExecutor streamingTaskExecutor,
            StreamingCorsProperties corsProperties
    ) {
        return new WebMvcConfigurer() {
            @Override
            public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
                configurer.setTaskExecutor(streamingTaskExecutor);
                configurer.setDefaultTimeout(0);
            }

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/wikipedia/recent-changes")
                        .allowedOrigins(corsProperties.allowedOrigins())
                        .allowedMethods("GET");
            }
        };
    }
}
