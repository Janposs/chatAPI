package com.example.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    private UserRepository userRepo;

    public Controller() {
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello world";
    }

    @PostMapping("/register")
    public String register(@RequestBody UserDTO user) {
        var u = userRepo.findByUserName(user.getUserName());
        if (u != null) {
            return "user exists already";
        }

        //not sure if this is usefull
        u = new User();
        u.setUserName(user.getUserName());
        u.setPassword(user.getPassword());
        userRepo.save(u);
        return "registration successfull";
    }

    @PostMapping("/login")
    public String login(@RequestBody UserDTO user) {
        var usr = userRepo.findByUserName(user.getUserName());
        if (usr != null) {
            return user.toString();
        }

        return "user not found";
    }
}
