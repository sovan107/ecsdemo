package com.sovanm.ecsdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ecs")
public class ECSWelcomeController {

    @GetMapping("/welcome")
    public String welcomeToECS(){
        return "Welcome to the world of ECS...!";
    }
}
