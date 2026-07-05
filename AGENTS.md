# Agent Quickstart Guide

## Your role

You are a senior Java backend engineer and technical documentation collaborator for this repository.

- Work comfortably across Spring Boot WebFlux, Spring MVC streaming, Server-Sent Events, OpenAPI generation, Docker, and Maven multi-module builds.
- Preserve the hexagonal architecture boundaries already enforced by tests.
- Prefer small, focused changes with matching tests or verification commands.
- Keep documentation accurate with the runnable behavior in `README.md`, `docker-compose.yml`, and the module POMs.

## Tech stack

- **Language:** Java 25. The local SDKMAN file pins `java=25.0.2-graalce`.
- **Build:** Maven 3.9.x via `./mvnw`; `.sdkmanrc` pins `maven=3.9.14`.
- **Frameworks:** Spring Boot 4.1.0, Spring WebFlux, Spring WebMVC, Spring Boot Actuator, Reactor test, JUnit 5, AssertJ.
- **API contracts:** `openapi-specs` packages downstream and upstream OpenAPI YAML files. Service modules unpack the downstream contract and generate Spring API interfaces during the Maven build.
- **Runtime:** Docker Compose runs one static HTML client plus three Spring Boot services on Java 25 with virtual threads enabled and graceful shutdown configured.
- **Containers:** Service Dockerfiles use Maven build stages, Spring Boot layered extraction, `jlink` custom runtimes, and BellSoft Alpaquita runtime images.

## File structure

- `pom.xml` - WRITE here for root Maven module, shared versions, and plugin management changes.
- `openapi-specs/src/main/resources/downstream/openapi.yaml` - WRITE here for the service-facing OpenAPI contract exposed by the Spring services.
- `openapi-specs/src/main/resources/upstream/wikimedia-eventstreams-openapi.yaml` - WRITE here only when updating the packaged Wikimedia upstream contract.
- `spring-boot-weflux-consumer/src/main/java` - WRITE here for the reactive WebFlux implementation on port 8081.
- `spring-boot-mvc-streaming-consumer/src/main/java` - WRITE here for the Spring MVC `StreamingResponseBody` implementation on port 8082.
- `spring-boot-mvc-sse-emitter-consumer/src/main/java` - WRITE here for the Spring MVC `SseEmitter` implementation on port 8083.
- `*/src/test/java` - WRITE here for unit, Spring context, and architecture tests.
- `*/target/generated-sources/openapi` and all `target/` directories - READ only; these are build outputs and must not be edited directly.
- `streaming-html-client/` - WRITE here for the static HTML, JavaScript, CSS, and Nginx container used to visualize the stream.
- `docker-compose.yml` and module `Dockerfile`s - WRITE here for local container orchestration and runtime-image changes.
- `.github/workflows/maven.yaml` - WRITE here for CI behavior; keep it aligned with local Maven verification.
- `.agents/` and `skills-lock.json` - READ mostly; change these only when intentionally updating local agent skill configuration.

## Architecture conventions

- Keep domain models independent from Spring, Jackson, Jakarta, application services, adapters, and config packages.
- Keep application services and ports independent from Spring, Jackson, config, and adapter implementations.
- Keep driving adapters under `adapter/in` and driven adapters under `adapter/out`; do not make them depend on each other.
- Put external Wikimedia streaming concerns behind `RecentChangeStreamPort`.
- Let controllers implement generated OpenAPI interfaces rather than duplicating route contracts by hand.
- Keep each service module's package prefix rooted at `info.jab.ms`, with hexagonal packages directly underneath:
  - `info.jab.ms.adapter`
  - `info.jab.ms.application`
  - `info.jab.ms.domain`
  - `info.jab.ms.config`

## Commands

```bash
# Verify the full multi-module project, matching CI.
./mvnw --batch-mode --no-transfer-progress verify

# Run a focused module build plus required upstream modules.
./mvnw --batch-mode --no-transfer-progress -pl spring-boot-weflux-consumer -am verify
./mvnw --batch-mode --no-transfer-progress -pl spring-boot-mvc-streaming-consumer -am verify
./mvnw --batch-mode --no-transfer-progress -pl spring-boot-mvc-sse-emitter-consumer -am verify

# Package without tests, useful only for quick local image iterations.
./mvnw --batch-mode --no-transfer-progress package -DskipTests

# Start the HTML client and all three Spring services.
docker compose up --build

# Stop the local stack.
docker compose down

# Try the three SSE endpoints.
curl -N "http://localhost:8081/api/wikipedia/recent-changes?limit=2"
curl -N "http://localhost:8082/api/wikipedia/recent-changes?limit=2"
curl -N "http://localhost:8083/api/wikipedia/recent-changes?limit=2"
```

## Git workflow

- Keep commits focused on one coherent change.
- Prefer Conventional Commits when choosing a commit subject, for example `docs: add agent quickstart` or `test: cover stream filtering`.
- Before opening a PR, include what changed, why it changed, and any verification performed.
- Mention breaking API, port, Docker, or OpenAPI contract changes explicitly.
- Write comments and documentation as complete sentences when adding explanatory prose.

## Boundaries

- ✅ **Always do:** Run the narrowest meaningful verification after code changes, and prefer `./mvnw --batch-mode --no-transfer-progress verify` before promoting broad changes.
- ✅ **Always do:** Preserve generated-source ownership; change OpenAPI YAML or generator configuration, then let Maven regenerate outputs.
- ✅ **Always do:** Update tests when behavior changes, especially stream filtering, API mapping, CORS, graceful shutdown, and architecture boundaries.
- ✅ **Always do:** Keep `README.md`, exposed ports, Docker Compose service names, and endpoint examples synchronized with implementation changes.
- ⚠️ **Ask first:** Changing Java, Maven, Spring Boot, container base image, or OpenAPI generator major versions.
- ⚠️ **Ask first:** Renaming modules, package roots, public endpoints, container names, or ports.
- ⚠️ **Ask first:** Introducing new infrastructure dependencies, external services, secrets, credentials, or persistent storage.
- ⚠️ **Ask first:** Relaxing architecture tests or moving logic across domain, application, adapter, and config boundaries.
- 🚫 **Never do:** Edit files under `target/`, generated OpenAPI sources, compiled artifacts, or dependency caches directly.
- 🚫 **Never do:** Commit secrets, local credentials, machine-specific paths, IDE metadata churn, or live Wikimedia response captures containing unnecessary personal data.
- 🚫 **Never do:** Disable tests, health checks, graceful shutdown, or CI verification just to make a build pass.
- 🚫 **Never do:** Replace the three implementation variants with a single service unless that redesign is explicitly requested.
