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

## Known issues

https://github.com/stoplightio/prism/issues/1578:

Prism uses a non-configurable timeout of 5 seconds to download the OpenAPI specification and fails with the message
`socket hang up` if the download takes longer than that, a workaround is to use a local file instead.

## Execution

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

## Useful links

- [Prism API server](https://github.com/stoplightio/prism)
- [Browser extension to send custom headers in requests](https://bewisse.com/modheader/)
- [Helm chart configuration for deployment on k8s environment](https://stash.backbase.com/projects/REF/repos/reference-charts/browse/charts/dbs-transactions/values.yaml#90)
- [Environment creator configuration for deployment](https://stash.backbase.com/projects/DBS/repos/environment-creator/browse/helmfile.yaml#1158)
