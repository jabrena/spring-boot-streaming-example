FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml .
COPY openapi-specs/pom.xml openapi-specs/pom.xml
COPY spring-boot-weflux-consumer/pom.xml spring-boot-weflux-consumer/pom.xml
COPY spring-boot-mvc-streaming-consumer/pom.xml spring-boot-mvc-streaming-consumer/pom.xml
COPY spring-boot-mvc-sse-emitter-consumer/pom.xml spring-boot-mvc-sse-emitter-consumer/pom.xml
RUN mvn -B -pl spring-boot-mvc-streaming-consumer -am dependency:go-offline

COPY openapi-specs/src openapi-specs/src
COPY spring-boot-mvc-streaming-consumer/src spring-boot-mvc-streaming-consumer/src
RUN mvn -B -pl spring-boot-mvc-streaming-consumer -am package -DskipTests
RUN java -Djarmode=tools -jar spring-boot-mvc-streaming-consumer/target/*.jar extract --layers --destination extracted

FROM eclipse-temurin:25-jdk AS custom-jre
RUN jlink \
    --add-modules java.base,java.compiler,java.desktop,java.instrument,java.logging,java.management,java.naming,java.net.http,java.prefs,java.scripting,java.security.jgss,java.sql,java.xml,jdk.crypto.ec,jdk.jfr,jdk.management,jdk.unsupported \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --compress zip-6 \
    --output /custom-jre

FROM bellsoft/alpaquita-linux-base:glibc
WORKDIR /app

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=custom-jre /custom-jre ${JAVA_HOME}
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

EXPOSE 8082

HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=5 \
  CMD wget -q -O - http://localhost:8082/actuator/health > /dev/null || exit 1

ENTRYPOINT ["java", "-cp", "/app/*:/app/lib/*", "com.example.mvcstreamingconsumer.MainApplication"]
