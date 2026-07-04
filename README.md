# spring-boot-streaming-example

Reactive streaming example that consumes Wikimedia recent-change events and
shows them in a small HTML client.

## Run

From the repository root:

```bash
docker compose up --build
```

Docker Compose starts:

- `wikipedia-service`: Spring Boot WebFlux API on `http://localhost:8080`
- `wikipedia-mvc-service`: Spring MVC API on `http://localhost:8082`
- `streaming-html-client`: HTML/JavaScript client on `http://localhost:8081`

Both Spring services run on Java 25 with virtual threads enabled. Their runtime
containers use BellSoft Alpaquita base images, Spring Boot layered extraction,
and a custom `jlink` Java runtime to keep image size small. Container health
checks use the `wget` binary already available in the base image, so no extra
`curl` package is installed. The HTML client waits until both Spring Boot
services are healthy before starting.

## Visualize The Stream

Open:

```text
http://localhost:8081
```

Click `Connect` to start receiving live Wikipedia recent-change events.

Use the `Source` selector to choose the backend implementation:

- `WebFlux`: `http://localhost:8080/api/wikipedia/recent-changes`
- `MVC`: `http://localhost:8082/api/wikipedia/recent-changes`

## Local Streaming Endpoint

Both backends expose the same Server-Sent Events endpoint:

```text
GET /api/wikipedia/recent-changes
```

Useful examples:

```bash
curl -N "http://localhost:8080/api/wikipedia/recent-changes?limit=2"
curl -N "http://localhost:8082/api/wikipedia/recent-changes?limit=2"
curl -N "http://localhost:8080/api/wikipedia/recent-changes?wiki=enwiki&limit=5"
curl -N "http://localhost:8080/api/wikipedia/recent-changes?wiki=enwiki&includeBots=true&limit=5"
```

Stop everything:

```bash
docker compose down
```

The Spring Boot services use graceful shutdown with a 30-second shutdown phase,
and Docker Compose gives each service 35 seconds before forcing termination.

## Data Source

The Spring Boot service consumes this public Wikimedia SSE stream:

```text
https://stream.wikimedia.org/v2/stream/recentchange
```

Wikimedia stream documentation:

```text
https://wikitech.wikimedia.org/wiki/Event_Platform/EventStreams_HTTP_Service
```

## OpenAPI And Schema

Wikimedia EventStreams OpenAPI:

```text
https://stream.wikimedia.org/?spec
```

Wikimedia Swagger UI:

```text
https://stream.wikimedia.org/?doc
```

Recent-change event JSON schema:

```text
https://schema.wikimedia.org/repositories/primary/jsonschema/mediawiki/recentchange/latest
```
