package info.jab.ms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.web.router.Router;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MainApplicationTest {

    @Test
    void healthEndpointIsAvailable() {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of("micronaut.server.port", "-1"));
             HttpClient client = server.getApplicationContext().createBean(HttpClient.class, server.getURL())) {
            var response = client.toBlocking().exchange("/health", Map.class);

            assertEquals(HttpStatus.OK, response.status());
            assertEquals("UP", response.body().get("status"));
        }
    }

    @Test
    void openApiContractIsServed() {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of("micronaut.server.port", "-1"));
             HttpClient client = server.getApplicationContext().createBean(HttpClient.class, server.getURL())) {
            var response = client.toBlocking().exchange("/openapi.yaml", String.class);

            assertEquals(HttpStatus.OK, response.status());
        }
    }

    @Test
    void recentChangesRouteIsRegistered() {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Map.of("micronaut.server.port", "-1"))) {
            Router router = server.getApplicationContext().getBean(Router.class);

            assertTrue(router.route(HttpMethod.GET, "/api/wikipedia/recent-changes").isPresent());
        }
    }
}
