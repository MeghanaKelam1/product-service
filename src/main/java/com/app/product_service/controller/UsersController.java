package com.app.product_service.controller;

import com.app.product_service.model.Users;
import com.app.product_service.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/products")
public class UsersController {

    @Autowired
    private UsersService service;

    @PostMapping("/register")
    public Users register(@RequestBody Users user){
        System.out.println("controller");
        return service.register(user);
    }
    @PostMapping("/login")
    public String login(@RequestBody Users user){
        return service.verify(user);
    }
}
