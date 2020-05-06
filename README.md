# API Simulator

## Description

A service that's capable of registering itself in a service registry and simulate request handling based solely
on an API specification and the examples it contains.

This allows simulating services that have not been implemented yet or will not be implemented by Backbase because
they're meant to be implemented by external parties such as integration APIs.

## Requirements

- an OpenAPI specification

## Setup

Build it as a regular maven project with `mvn clean install`.

## Configuration

Typically the following properties need to be configured:

- `spring.application.name`: name of the service that you'll simulate, `card-manager` for example
- `backbase.api.simulator.basePath`: URL path prepended to all API calls, `/client-api/v2` for example
- `backbase.api.simulator.spec`: OpenAPI specification file path or URL that will be simulated

## Execution

### Locally

Run it as a Spring Boot application with working directory pointing to the root directory of this project.
Use `com.backbase.api.simulator.Application` as main class and `local` profile.

### Docker

Execute the following command from the project's root directory to run it with Docker:

`docker run --env-file ./config/env.list -p 14080:14080 --rm -it harbor.backbase.eu/staging/api-simulator:<version>`

Where:
- `<version` is an image version available on [Harbor](https://harbor.backbase.eu/harbor/projects), set it to `latest`
to run the latest version.

#### Running on Docker with a local OpenAPI spec file

The following command can be used to point the latest version of `api-simulator` to an OpenAPI specification file
located in the `config` folder:

`docker run -v $(pwd)/config/openapi.yaml:/config/openapi.yaml --env-file ./config/env.list -p 14080:14080 --rm -it harbor.backbase.eu/staging/api-simulator:latest`

Make sure `BACKBASE_API_SIMULATOR_SPEC` is set to `/config/openapi.yaml` in `./config/env.list`.
