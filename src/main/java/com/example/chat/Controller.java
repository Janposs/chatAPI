package com.example.chat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    public Controller() {
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello world";
    }
}
