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
- `streaming-html-client`: HTML/JavaScript client on `http://localhost:8081`

The HTML client waits until the Spring Boot service is healthy before starting.

## Visualize The Stream

Open:

```text
http://localhost:8081
```

Click `Connect` to start receiving live Wikipedia recent-change events.

## Local Streaming Endpoint

The backend exposes a Server-Sent Events endpoint:

```text
GET http://localhost:8080/api/wikipedia/recent-changes
```

Useful examples:

```bash
curl -N "http://localhost:8080/api/wikipedia/recent-changes?limit=2"
curl -N "http://localhost:8080/api/wikipedia/recent-changes?wiki=enwiki&limit=5"
curl -N "http://localhost:8080/api/wikipedia/recent-changes?wiki=enwiki&includeBots=true&limit=5"
```

Stop everything:

```bash
docker compose down
```

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
