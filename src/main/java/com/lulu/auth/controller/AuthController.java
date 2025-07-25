package com.lulu.auth.controller;

import com.lulu.auth.dto.*;
import com.lulu.auth.dto.LoginRequest;
import com.lulu.auth.dto.LoginResponse;
import com.lulu.auth.dto.RegisterRequest;
import com.lulu.auth.dto.RegisterResponse;
import com.lulu.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return authService.login(request, httpRequest);
    }

    //clerk
    @PostMapping("/register/clerk")
    public ResponseEntity<RegisterResponse> registerClerk(@RequestBody ClerkRequest request) {
        return ResponseEntity.ok(authService.registerWithClerk(request));
    }
    @PostMapping("/clerk-login")
    public ResponseEntity<LoginResponse> loginWithClerk(@RequestBody ClerkRequest request) {
        LoginResponse response = authService.loginWithClerk(request);
        return ResponseEntity.ok(response);
    }

}
