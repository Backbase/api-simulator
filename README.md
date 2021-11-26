# API Simulator

## Description

A service that's capable of registering itself in a service registry and do the following:
- simulate request handling based solely on an API specification and the examples it contains with [Prism](https://github.com/stoplightio/prism)
- simulate request handling based on stub mappings with [WireMock](http://wiremock.org/)

This allows simulating services that have not been implemented yet or will not be implemented by Backbase because
they're meant to be implemented by external parties such as integration APIs.

It's also possible to mock external dependencies so that performance tests provide consistent results and don't fail
because of external dependency failures.

## Requirements

- an OpenAPI specification or stub mappings

## Setup

Build it as a regular maven project with `mvn clean install`.

## Configuration

The following properties are always configured:

- `spring.application.name`: name of the service that you'll simulate, `card-manager` for example

One of the following needs to be configured:

- `backbase.api.simulator.spec`: OpenAPI specification file path or URL for Prism that will be simulated
- `mappings directory`: directory containing stub mappings for WireMock

## Known issues

https://github.com/stoplightio/prism/issues/1578:

Prism uses a non-configurable timeout of 5 seconds to download the OpenAPI specification and fails with the message
`socket hang up` if the download takes longer than that, a workaround is to use a local file instead.

## Execution with Prism

### Preparation

Download an OpenAPI specification and copy it to `config/openapi.yaml`.
It's also possible to configure a URL so that the service can download it at startup.

### Locally

Run it as a Spring Boot application with working directory pointing to the root directory of this project.
Use `com.backbase.api.simulator.Application` as main class, `local` profile and configure it with
`config/application-local.yml` file.

### Docker

Execute the following command to generate a docker image:

`mvn clean install jib:dockerBuild -Pdocker-image`

Then you can run the service with:

`docker run -v $(pwd)/config/openapi.yaml:/config/openapi.yaml --env-file ./config/env.list -p 8080:8080 --rm -it your-image-name`

## Execution with WireMock

### Preparation

Create stub mappings according to [WireMock's documentation](http://wiremock.org/docs/stubbing/).

Stub mappings must be in a directory called `mappings`.

### Locally

Run it as a Spring Boot application with working directory pointing to the root directory of this project.
Use `com.backbase.api.simulator.Application` as main class, `local` profile and configure it with
`config/application-local.yml` file.

### Docker

The following docker-compose runs `api-simulator` with WireMock:

```yaml
  access-control:
    image: harbor.backbase.eu/internal/api-simulator:${API_SIMULATOR_VERSION}
    environment:
      spring.application.name: access-control
      backbase.api.simulator.mode: PERFORMANCE
      backbase.api.simulator.port: 8080
      server.port: 18080
    volumes:
      - ./performance/access-control:/config
```

Where the directory `./performance/access-control/mappings` contains JSON stub mapping files.

## Useful links

- [Prism API server](https://github.com/stoplightio/prism)
- [Browser extension to send custom headers in requests](https://bewisse.com/modheader/)
- [WireMock on GitHub](https://github.com/wiremock/wiremock)
