Feature: Welcome message

  Scenario: A user requests the welcome message
    When the client calls "/ecs/welcome"
    Then the client receives status code 200
    And the client receives the message "Welcome to the world of ECS...!"

  Scenario: A user requests a non-existent endpoint
    When the client calls "/ecs/nonexistent"
    Then the client receives status code 404
