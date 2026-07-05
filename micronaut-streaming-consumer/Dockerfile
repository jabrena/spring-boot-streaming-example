FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml .
COPY openapi-specs openapi-specs
COPY micronaut-streaming-consumer micronaut-streaming-consumer
RUN mvn -B -N install \
    && mvn -B -f openapi-specs/pom.xml install -DskipTests \
    && mvn -B -f micronaut-streaming-consumer/pom.xml package -DskipTests

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
COPY --from=build --chown=10001:10001 /workspace/micronaut-streaming-consumer/target/micronaut-streaming-consumer-0.1.0.jar /app/app.jar

EXPOSE 8085
USER 10001:10001

HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=5 \
  CMD wget -q -O - http://localhost:8085/health > /dev/null || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
