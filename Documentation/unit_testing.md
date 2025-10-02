
### How to Run This Test Individually

To execute only the unit test for the controller, you can use Maven's `-Dtest` flag to specify the exact test class. This is much faster than running the entire test suite, especially as your project grows.

Run the following command from your project's root directory:

```bash
./mvnw test -Dtest=ECSWelcomeControllerTest
```


# The Importance and Setup of Unit Testing

## What is Unit Testing?

Unit testing is a level of software testing where individual components or "units" of a software are tested in isolation. The primary goal is to validate that each unit of the software performs as designed.

A unit can be a single function, method, procedure, module, or object. In our case, the unit is the `ECSWelcomeController`.

## Why is it Important?

1.  **Fast Feedback & Finding Bugs Early**: Because unit tests focus on a small piece of code, they run extremely fast. This allows developers to run them frequently as they code, catching issues and regressions almost instantly. Fixing a bug at this stage is far cheaper and faster than finding it later in the development cycle.

2.  **Improves Code Quality & Design**: Writing testable code often leads to better-designed, more modular, and loosely coupled code. If a component is hard to test, it's often a sign that its design could be improved.

3.  **Provides a Safety Net for Refactoring**: When you need to change or improve existing code (refactoring), a solid suite of unit tests acts as a safety net. After making changes, you can run the tests to ensure that you haven't accidentally broken any existing functionality.

4.  **Acts as Living Documentation**: A well-written unit test can serve as documentation for a piece of code. It clearly and concisely demonstrates how the code is intended to be used and what its expected behavior is.

## How Was It Set Up in Your Project?

We created a unit test for the `ECSWelcomeController` to verify its logic in isolation, without starting the entire application.

**File Location**: `src/test/java/com/sovanm/ecsdemo/controller/ECSWelcomeControllerTest.java`

### The Code:

```java
package com.sovanm.ecsdemo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 1. Specify the test setup
@WebMvcTest(ECSWelcomeController.class)
public class ECSWelcomeControllerTest {

    // 2. Inject a mock MVC client
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnWelcomeMessage() throws Exception {
        // 3. Perform and assert
        mockMvc.perform(get("/ecs/welcome"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome to the world of ECS...!"));
    }
}
```

### Key Components Explained:

1.  **`@WebMvcTest(ECSWelcomeController.class)`**: This is the core annotation for this test. It tells Spring Boot to set up a test environment that contains *only* the web layer (i.e., the specified controller and related MVC components). It does **not** load the full application context, which is why it's so fast. We are testing *only* the controller.

2.  **`@Autowired private MockMvc mockMvc;`**: `MockMvc` is a powerful testing utility that allows you to send mock HTTP requests to your controller and inspect the results, all without needing a real web server. It simulates the behavior of the Spring MVC dispatcher servlet.

3.  **`mockMvc.perform(...)`**: This line executes the test. We use it to perform a `GET` request to the `/ecs/welcome` endpoint and then chain assertions:
    *   `.andExpect(status().isOk())`: Verifies that the HTTP response status is 200 (OK).
    *   `.andExpect(content().string(...))`: Verifies that the response body is exactly the welcome message we expect.
