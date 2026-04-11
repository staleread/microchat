# micro-chat

## Development

### Prerequisites

- Java 21
- Docker

### Config files

The config server reads application configs from `~/.config/microchat/` (native profile).
Create a file there for each service, e.g. `core.yaml`, `assistant.yaml`.

When running via Docker Compose the same directory is mounted into the container, so no extra setup is needed.

### Building Docker images

```bash
./gradlew :config:bootBuildImage
./gradlew :eureka:bootBuildImage
# or all at once
./gradlew bootBuildImage
```

### Running infrastructure

```bash
docker compose up -d
```

`eureka` waits for `config` to pass its health check before starting.

### Running services locally

```bash
./gradlew :core:bootRun
./gradlew :assistant:bootRun
```
