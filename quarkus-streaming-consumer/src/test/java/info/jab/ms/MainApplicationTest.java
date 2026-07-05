package info.jab.ms;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class MainApplicationTest {

    @Test
    void healthEndpointIsAvailable() {
        given()
                .when().get("/q/health/live")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    void openApiContractIsServed() {
        given()
                .when().get("/openapi.yaml")
                .then()
                .statusCode(200);
    }
}
