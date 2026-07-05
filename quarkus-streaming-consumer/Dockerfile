FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml .
COPY openapi-specs/pom.xml openapi-specs/pom.xml
COPY spring-boot-weflux-consumer/pom.xml spring-boot-weflux-consumer/pom.xml
COPY spring-boot-mvc-streaming-consumer/pom.xml spring-boot-mvc-streaming-consumer/pom.xml
COPY spring-boot-mvc-sse-emitter-consumer/pom.xml spring-boot-mvc-sse-emitter-consumer/pom.xml
COPY quarkus-streaming-consumer/pom.xml quarkus-streaming-consumer/pom.xml
RUN mvn -B -pl quarkus-streaming-consumer -am dependency:go-offline

COPY openapi-specs/src openapi-specs/src
COPY quarkus-streaming-consumer/src quarkus-streaming-consumer/src
RUN mvn -B -pl quarkus-streaming-consumer -am package -DskipTests

FROM eclipse-temurin:25-jdk AS custom-jre
RUN jlink \
    --add-modules java.base,java.compiler,java.desktop,java.instrument,java.logging,java.management,java.naming,java.net.http,java.prefs,java.scripting,java.security.jgss,java.sql,java.xml,jdk.crypto.ec,jdk.jfr,jdk.management,jdk.unsupported,jdk.zipfs \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --compress zip-6 \
    --output /custom-jre

FROM bellsoft/alpaquita-linux-base:glibc
WORKDIR /app

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"

COPY --from=custom-jre /custom-jre ${JAVA_HOME}
COPY --from=build --chown=10001:10001 /workspace/quarkus-streaming-consumer/target/quarkus-app/lib/ /app/lib/
COPY --from=build --chown=10001:10001 /workspace/quarkus-streaming-consumer/target/quarkus-app/*.jar /app/
COPY --from=build --chown=10001:10001 /workspace/quarkus-streaming-consumer/target/quarkus-app/app/ /app/app/
COPY --from=build --chown=10001:10001 /workspace/quarkus-streaming-consumer/target/quarkus-app/quarkus/ /app/quarkus/

EXPOSE 8084
USER 10001:10001

HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=5 \
  CMD wget -q -O - http://localhost:8084/q/health/live > /dev/null || exit 1

ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
