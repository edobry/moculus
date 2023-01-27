# moculus design doc

## Requirements

Our goal is to create a "virtual orb", a "mock oculus" if you will, which is capable of substituting for a hardware Orb for the purposes of testing our systems. We have the following goals for this project; it should:

- report its own status to some monitoring system
- simulate signups by making requests to the backend
- be able to run locally or in a build system
- be covered with unit tests


We will be using Java for this project, in conjunction with the Spring Boot framework; not because its necessarily the best tool for the job, but rather because its the one I know best for this sort of problem. Given the nature of this project, I'm going to trade off concision and minimalism for velocity.

We will be using Gradle for builds, given that its the de facto standard for Java build tools, and that I'm most familiar with it.


The "status reporting" requirement is not explicitly phrased as such, but seems to be to be a standard instrumentation requirement; as such, I will approach it as I typically would, by exposing a Prometheus endpoint with the relevant metrics. Given though that this endpoint is a passive method, and the requirement calls for active, I will pair it with a `statsd` deployment, scraping the endpoint and submitting the metrics to whichever monitoring system we're using, which I'm assuming will understand the statsd format, as its relatively standard.

Given that we're using Spring, we can make use of the Actuator component (a Spring-native instrumentation framework) to generate our state metrics.

We will use a Scheduler component to periodically run signup simulation jobs, which will entail crafting an HTTP request containing the image and the ID (this is unclear; is this a randomly generated ID? an incremented counter? why is this necessary? to guard against backend failures?). 



Testing

I'm going to assume that we don't care about testing the instrumentation, as that's not typically considered necessary, and will instead focus on the signup simulation. We have several options here: a unit test validating only the contents of the crafted request, or an integration test involving a test API server receiving real network requests. For the sake of this project, I will take the unit test approach, to minimize complexity and focus on the actual business logic at play here.


Deployment

In order to allow for heterogenous deployment environments, it would be advantageous to containerize the application; we will use Jib for this, as its a Gradle-native method of producing a Docker image without requiring a Dockerfile to be written.
