# microchat

## Development

### Project setup

Install the following tools:

| Tool   | Version      | Notes               |
|--------|--------------|---------------------|
| Java   | 21 (Temurin) |                     |
| Gradle | 9            |                     |
| Docker | latest       | with Compose plugin |

If you use [Mise](https://mise.jdx.dev/), Java and Gradle are configured in
`mise.toml` and installed automatically with `mise install`. Mise also sets
`SPRING_PROFILES_ACTIVE=dev` in your shell, which is required when running
services locally.

If you don't use Mise, set the environment variable manually:

```bash
export SPRING_PROFILES_ACTIVE=dev
```

### Config files

Dev configs are committed under `config-repo/` and are picked up automatically
— no extra setup needed.

Production configs follow the `config-repo/*-prod.yaml` pattern and are
gitignored. Add them locally if needed.

### Building Docker images

```bash
gradle config:bootBuildImage
gradle eureka:bootBuildImage
# or all at once
gradle bootBuildImage
```

### Running infrastructure

```bash
docker compose up -d
```

### Running services locally

```bash
gradle core:bootRun
gradle assistant:bootRun
```
