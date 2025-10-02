
### How to Run This Test Individually

To execute only this acceptance test, you can specify the test class using Maven's `-Dtest` flag. This will start the full application for this single test, which is slower than a unit test but still much faster than running every test in the project.

Run the following command from your project's root directory:

```bash
./mvnw test -Dtest=ECSWelcomeControllerAcceptanceTest
```


# The Importance and Setup of Acceptance Testing

## What is Acceptance Testing?

Acceptance testing (also known as end-to-end testing or integration testing in this context) is a level of software testing where the complete, integrated software system is tested. The purpose is to evaluate the system's compliance with the business requirements and assess whether it is acceptable for delivery.

Unlike unit tests that check individual components in isolation, acceptance tests check that those components work together correctly.

## Why is it Important?

1.  **Confidence in the Whole System**: Acceptance tests verify that the different modules and services within your application are correctly integrated. They ensure that a user's journey through the application works as expected from start to finish.

2.  **Validates Business Requirements**: These tests are designed around user stories and business requirements. A passing acceptance test suite gives you high confidence that the application meets the functional requirements it was built for.

3.  **Catches Integration and Environmental Issues**: Many bugs only surface when different parts of a system interact or when the application is run in a realistic, server-like environment. Acceptance tests are excellent at catching these types of configuration, network, and integration errors.

4.  **Final Quality Gate**: They often serve as the final quality gate before deploying an application to production. If the acceptance tests pass, the application is considered ready for release.

## How Was It Set Up in Your Project?

We created an acceptance test that starts your entire Spring Boot application and makes a real HTTP request to it, just like a real client would.

**File Location**: `src/test/java/com/sovanm/ecsdemo/controller/ECSWelcomeControllerAcceptanceTest.java`

### The Code:

```java
package com.sovanm.ecsdemo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

// 1. Specify the test setup
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ECSWelcomeControllerAcceptanceTest {

    // 2. Inject the server port and a REST client
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnWelcomeMessage() {
        // 3. Perform a real HTTP request and assert
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/ecs/welcome", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Welcome to the world of ECS...!");
    }
}
```

### Key Components Explained:

1.  **`@SpringBootTest(webEnvironment = ...)`**: This annotation is fundamentally different from `@WebMvcTest`. It loads your *entire* application context and starts a real web server. `webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT` tells it to run on a randomly available port to avoid conflicts during testing.

2.  **`@LocalServerPort` & `@Autowired TestRestTemplate`**: Because we have a real server running, we need to know what port it's on. `@LocalServerPort` injects this port number into our `port` variable. The `TestRestTemplate` is a convenient HTTP client provided by Spring Boot for making requests to our test server.

3.  **`restTemplate.getForEntity(...)`**: This is the core of the test. Unlike the `MockMvc` which simulates requests, this line makes a **real HTTP network call** to the running application. We then inspect the `ResponseEntity` to assert that the status code and body are correct, just like any external client would.
