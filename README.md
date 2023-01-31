# moculus

This repository contains a system simulating an Orb for the purposes of development and/or testing. It is written in Java and uses the Spring Boot framework. The `gradle` build tool is used for dependency management, compilation, and packaging.

The design rationale is articulated in [this document](design.md).

## Features

The system includes two primary components, the `SignupSimulator` and the `StatusReporter`, and several other supporting components.

### `SignupSimulator`

This component repeatedly generates a random image and UUID, uploads the former to an object storage backend, and then sends an HTTP POST request to a backend API with the image URL and the aforementioned ID.

### `StatusReporter`

This component generates fake values for the following set of four metrics:
- battery
- cpu temperature
- cpu utilization
- disk utilization

These metrics are submitted to Micrometer.

### `SignupController`

This component is an embedded REST API consisting of the `POST /signup` endpoint, for testing the `SignupSimulator`. It can be disabled in production. 

### `ObjectStorageProvider`

This component provides access to an object storage backend for the `SignupSimulator` to upload images to. By default, it uses an in-memory S3-compatible server (`mock-backend`) and stores the uploaded objects on disk. In production, it can be configured to work with S3 or any S3-compatible system.

### Actuator

This component is the built-in Spring instrumentation & management framework; it is used to configure and host the metrics system, Micrometer. Both the Prometheus and Statsd registries are supported, but only the former is enabled by default.

With the bundled configuration, the metrics can be accessed at [this URL](http://localhost:8080/actuator/prometheus).

## Usage

On a POSIX-compatible system, use the included `gradelw` wrapper script to build, package, and run.

To build and execute the application:

```shell
./gradlew bootRun
```

To produce a Docker image, first ensure the daemon is running, and then:

```shell
./gradlew jibDockerBuild
```

To run the Docker container, you must either disable the `mock-backend`, or mount a directory for it to use to persist uploaded images, and provide its path to the application.

```shell
sudo docker run -v moculus-bucket:/opt/moculus/moculus-bucket:rw edobry/moculus -e MOCK_BACKEND_PATH=/opt/moculus/moculus-bucket
```

## Configuration

As we are using Spring, we can take advantage of the built-in configuration modalities, namely the `application.yaml` config file, as well as the automatic environment variable merge; that is, any known field in the file can also be set through an appropriately-named envvar.

For example, the field `mock-backend.path` can be set using the `MOCK_BACKEND_PATH` environment variable.

## Testing

This repository includes two JUnit test classes containing a rudimentary set of tests, convering the basic logic of the provided requirements.

## Contributing

Hire @edobry :)