package com.zhd4.taskmanager.controller;

import com.zhd4.taskmanager.dto.AuthRequest;
import com.zhd4.taskmanager.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    String create(@RequestBody AuthRequest authRequest) {
        return authenticationService.generateAuthToken(authRequest);
    }
}
