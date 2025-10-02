package com.sovanm.ecsdemo.controller;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WelcomeStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;

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
