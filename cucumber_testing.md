
### How to Run This Test Individually

To execute only your Cucumber tests, you target the test runner class (`CucumberRunnerTest`) with Maven's `-Dtest` flag. This will trigger the runner, which will then find and execute all your feature files.

Run the following command from your project's root directory:

```bash
./mvnw test -Dtest=CucumberRunnerTest
```

If you want to run just a single feature file, you can do so by pointing Cucumber to the specific file path:

```bash
./mvnw test -Dtest=CucumberRunnerTest -Dcucumber.features="src/test/resources/features/welcome.feature"
```


# The Importance and Setup of BDD with Cucumber

## What is BDD and Cucumber?

**Behavior-Driven Development (BDD)** is a software development approach that enhances communication between technical and non-technical stakeholders. It focuses on defining and testing the desired *behavior* of the system from a user's perspective, using a natural, human-readable language.

**Cucumber** is the leading tool for implementing BDD. It allows you to write executable specifications in a language called **Gherkin**. These specifications act as both documentation and automated tests.

## Why is it Important?

1.  **Shared Understanding (Living Documentation)**: The primary benefit of BDD is creating a single source of truth that everyone on the team—developers, QAs, product managers, and business analysts—can understand and contribute to. The Gherkin feature files become "living documentation" that is always in sync with the application's actual behavior.

2.  **Focus on Business Value**: BDD shifts the focus from testing implementation details to testing user-facing behavior that delivers business value. Scenarios are written in terms of user goals and outcomes (e.g., "When a user requests the welcome message, they should see a greeting").

3.  **Improved Collaboration**: It provides a common language that bridges the gap between technical and business teams. This reduces misunderstandings, clarifies requirements, and ensures that the software being built is the software that is actually needed.

4.  **Automated Acceptance Criteria**: Each scenario in a feature file represents an acceptance criterion for a user story. By automating these scenarios, you are directly automating your acceptance tests in a highly readable format.

## How Was It Set Up in Your Project?

We implemented a BDD test for the welcome feature using Cucumber, which involved three main parts:

### 1. The Feature File (The "What")

This plain text file describes the feature's behavior in Gherkin syntax. It's the specification. We can include multiple scenarios, including "happy path" (correct usage) and "unhappy path" (error conditions).

**File Location**: `src/test/resources/features/welcome.feature`

```gherkin
Feature: Welcome message

  Scenario: A user requests the welcome message
    When the client calls "/ecs/welcome"
    Then the client receives status code 200
    And the client receives the message "Welcome to the world of ECS...!"

  Scenario: A user requests a non-existent endpoint
    When the client calls "/ecs/nonexistent"
    Then the client receives status code 404
```

### 2. The Step Definitions (The "How")

This Java class acts as the "glue" that translates each step from the Gherkin feature file into executable Java code. Note how the `@When` step is now generic and can be reused for both scenarios.

**File Location**: `src/test/java/com/sovanm/ecsdemo/controller/WelcomeStepDefinitions.java`

```java
// ... imports

// 1. Link to the Spring context
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WelcomeStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;
    private ResponseEntity<String> response;

    // 2. Map Gherkin steps to methods
    @When("the client calls {string}")
    public void theClientCalls(String path) {
        response = restTemplate.getForEntity(path, String.class);
    }

    @Then("the client receives status code {int}")
    public void theClientReceivesStatusCode(int statusCode) {
        assertThat(response.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @Then("the client receives the message {string}")
    public void theClientReceivesTheMessage(String message) {
        assertThat(response.getBody()).isEqualTo(message);
    }
}
```

*   **`@CucumberContextConfiguration`**: This annotation tells Cucumber to use the Spring Boot application context, allowing us to use `@SpringBootTest` and inject dependencies like `TestRestTemplate`.
*   **`@When`, `@Then`**: These annotations link the methods to the corresponding steps in `welcome.feature`. Cucumber uses the text in the annotation (a regular or Cucumber expression) to find the right method to execute.

### 3. The Test Runner (The "Executor")

This class tells JUnit how to find and run the Cucumber tests.

**File Location**: `src/test/java/com/sovanm/ecsdemo/CucumberRunnerTest.java`

```java
package com.sovanm.ecsdemo;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features") // Points to our features directory
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.sovanm.ecsdemo.controller") // Tells Cucumber where to find step definitions
public class CucumberRunnerTest {
}
```
