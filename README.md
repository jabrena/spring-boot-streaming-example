# multi-framework-streaming-example

Reactive streaming example that consumes Wikimedia recent-change events and
shows them in a small HTML client.

## Run

From the repository root:

```bash
docker compose up --build
```

Docker Compose starts:

- `streaming-html-client`: HTML/JavaScript client on `http://localhost:8080`
- `wikipedia-webflux-service`: Spring Boot WebFlux API on `http://localhost:8081`
- `wikipedia-mvc-service`: Spring MVC `StreamingResponseBody` API on `http://localhost:8082`
- `wikipedia-mvc-sse-emitter-service`: Spring MVC `SseEmitter` API on `http://localhost:8083`
- `wikipedia-quarkus-service`: Quarkus REST API on `http://localhost:8084`
- `wikipedia-micronaut-service`: Micronaut HTTP API on `http://localhost:8085`

All backend services run on Java 25. The Spring services enable virtual threads,
and the Quarkus and Micronaut services use virtual threads for each upstream SSE
subscription. Their runtime containers use BellSoft Alpaquita base images and
custom `jlink` Java runtimes to keep image size small. Container health checks
use the `wget` binary already available in the base image, so no extra `curl`
package is installed. The HTML client waits until all backend services are
healthy before starting.

The Maven root project binds the three Spring Boot services, the Quarkus service,
the Micronaut service, and a shared `openapi-specs` module. Each service depends
on that module and serves the same output OpenAPI contract at `/openapi.yaml`.
The `openapi-specs` module packages that downstream service contract from
`downstream/openapi.yaml` plus the upstream Wikimedia EventStreams contract in
`upstream/wikimedia-eventstreams-openapi.yaml`. Each Spring service unpacks the
module's `openapi` classifier and generates a local API interface from the
downstream contract during the Maven build. The MVC consumers also generate
local model classes, while the WebFlux, Quarkus, and Micronaut consumers keep
their reactive model code local.

## Runtime Resource Snapshot

Example `docker stats --no-stream` output with the full stack running:

| Container ID | Name | CPU % | Memory Usage / Limit | Memory % | Net I/O | Block I/O | PIDs |
| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: |
| `b7afae0298fe` | `wikipedia-micronaut-service` | 20.25% | 147.6MiB / 1GiB | 14.42% | 360kB / 36.8kB | 0B / 0B | 32 |
| `0f36cefed5fa` | `wikipedia-mvc-sse-emitter-service` | 0.08% | 158.4MiB / 1GiB | 15.47% | 1.9kB / 126B | 0B / 32.8kB | 22 |
| `b3088c285284` | `wikipedia-mvc-service` | 0.22% | 160.2MiB / 1GiB | 15.64% | 2.01kB / 264B | 0B / 32.8kB | 22 |
| `4d8f91c6b916` | `wikipedia-quarkus-service` | 0.23% | 77.36MiB / 1GiB | 7.55% | 1.97kB / 264B | 0B / 0B | 25 |
| `9cd04f5e26b7` | `wikipedia-webflux-service` | 0.07% | 156.2MiB / 1GiB | 15.26% | 1.78kB / 126B | 0B / 32.8kB | 25 |
| `6edd41f31fd8` | `streaming-html-client` | 0.00% | 7.051MiB / 1GiB | 0.69% | 1.13kB / 318B | 0B / 8.19kB | 9 |

## Visualize The Stream

Open:

```text
http://localhost:8080
```

Click `Connect` to start receiving live Wikipedia recent-change events.

Use the `Source` selector to choose the backend implementation:

- `WebFlux`: `http://localhost:8081/api/wikipedia/recent-changes`
- `MVC StreamingResponseBody`: `http://localhost:8082/api/wikipedia/recent-changes`
- `MVC SseEmitter`: `http://localhost:8083/api/wikipedia/recent-changes`
- `Quarkus REST`: `http://localhost:8084/api/wikipedia/recent-changes`
- `Micronaut HTTP`: `http://localhost:8085/api/wikipedia/recent-changes`

## Local Streaming Endpoint

Both backends expose the same Server-Sent Events endpoint:

```text
GET /api/wikipedia/recent-changes
```

Useful examples:

```bash
curl -N "http://localhost:8081/api/wikipedia/recent-changes?limit=2"
curl -N "http://localhost:8082/api/wikipedia/recent-changes?limit=2"
curl -N "http://localhost:8083/api/wikipedia/recent-changes?limit=2"
curl -N "http://localhost:8084/api/wikipedia/recent-changes?limit=2"
curl -N "http://localhost:8085/api/wikipedia/recent-changes?limit=2"
curl -N "http://localhost:8081/api/wikipedia/recent-changes?wiki=enwiki&limit=5"
curl -N "http://localhost:8081/api/wikipedia/recent-changes?wiki=enwiki&includeBots=true&limit=5"
```

Stop everything:

```bash
docker compose down
```

The backend services use graceful shutdown with a 30-second shutdown phase, and
Docker Compose gives each service 35 seconds before forcing termination.

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

Local output OpenAPI specification exposed by this project:

```text
http://localhost:8081/openapi.yaml
http://localhost:8082/openapi.yaml
http://localhost:8083/openapi.yaml
http://localhost:8084/openapi.yaml
http://localhost:8085/openapi.yaml
```

Packaged upstream input OpenAPI specification:

```text
openapi-specs/src/main/resources/upstream/wikimedia-eventstreams-openapi.yaml
```

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

## References

- https://openapi-generator.tech/
