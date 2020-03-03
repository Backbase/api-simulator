# API Simulator Service

## Description

A service that's capable of registering itself in a service registry and simulate request handling based solely
on a API specification and the examples it contains.

This allows simulating services that have not been implemented yet or will not be implemented by Backbase because
they're meant to be implemented by external parties such as integration APIs.

## Requirements

- Maven 3.x
- an OpenAPI specification

## Setup

Build it as a regular maven project with `mvn clean install`.

Run it as a Spring Boot application with working directory pointing to the root directory of this project.
Use `com.backbase.api.simulator.Application` as main class.

## Configuration

Typically the following properties are configured:

- `spring.application.name`: name of the service that you'll simulate, `card-manager` for example
- `backbase.api.simulator.basePath`: URL path prepended to all API calls, `/client-api/v2` for example
- `backbase.api.simulator.spec`: file path or URL of the OpenAPI specification that will be simulated
