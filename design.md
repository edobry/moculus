# moculus design doc

## Requirements

Our goal is to create a "virtual orb", a "mock oculus" if you will, which is capable of substituting for a hardware Orb for the purposes of testing our systems. We have the following goals for this project; it should:

- report its own status to some monitoring system
- simulate signups by making requests to the backend
- be able to run locally or in a build system
- be covered with unit tests


We will be using Java for this project, in conjunction with the Spring Boot framework; not because its necessarily the best tool for the job, but rather because its the one I know best for this sort of problem. Given the nature of this project, I'm going to trade off concision and minimalism for velocity.

We will be using Gradle for builds, given that its the de facto standard for Java build tools, and that I'm most familiar with it.

### Status reporting

The "status reporting" requirement is not explicitly phrased as such, but seems to be to be a standard instrumentation requirement; as such, I will approach it as I typically would, by exposing a Prometheus endpoint with the relevant metrics. Given though that this endpoint is a passive method, and the requirement calls for active, I will also support the `statsd` format, as its relatively standard.

### Image upload

For uploading images, we have two choices: we can either submit directly to the backend API, if it supports multipart-uploads, or, if we want to avoid the hassle of handling that correctly, as well as all the other associated concerns, we can instead offload this to S3, and instead submit image URLs to the backend. This approach has several tradeoffs, namely that it increases our attack surface, as we have to now be mindful of configuring the bucket properly, as well as managing object lifecycles so as to stay true to the user promise of not storing their iris images.

There's a middle-of-the-road approach here too; use the S3 API, but support swapping backends, such as to an self-hosted S3-compatible system like MinIO, or an in-memory S3 server. This would mean that we have the flexibility to change our image storage layer without changing any code, and we can avoid re-implementing image uploads.

### Architecture

Given that we're using Spring, we can make use of the Actuator component (a Spring-native instrumentation framework) to generate our state metrics.

We will use a Scheduler component to periodically run signup simulation jobs, which will entail crafting an HTTP request containing the image and the ID (this is unclear; is this a randomly generated ID? an incremented counter? why is this necessary? to guard against backend failures?). 

### Testing

I'm going to assume that we don't care about testing the instrumentation, as that's not typically considered necessary, and will instead focus on the signup simulation. We have several options here: a unit test validating only the contents of the crafted request, or an integration test involving a test API server receiving real network requests. For the sake of this project, I will take the unit test approach, to minimize complexity and focus on the actual business logic at play here.

#### Test Cases

##### SignupSimulator

- signups contain a URL
- signup URLs point to a downloadable file
- signup images are in a PNG format
- signup images are unique
- signups contain an ID
- signup IDs are unique


##### StatusReporter

- battery is reported
- cpu temperature is reported
- cpu utilization is reported
- disk utilization is reported

### Deployment

In order to allow for heterogeneous deployment environments, we will containerize the application with Jib, as its a Gradle-native method of producing a Docker image without requiring a Dockerfile to be written.

In reality, I would also pair this with a Helm chart for deploying to Kubernetes, but this is most likely out of scope.