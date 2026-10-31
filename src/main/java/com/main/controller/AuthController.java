package com.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.dto.LoginRequest;
import com.main.dto.LoginResponse;
import com.main.dto.RegisterRequest;
import com.main.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins="http://localhost:5173")
public class AuthController {
	
	 private final AuthService authService;

	    public AuthController(AuthService authService) {
	        this.authService = authService;
	    }

	    @PostMapping("/register")
	    public ResponseEntity<String> register(
	            @RequestBody RegisterRequest request) {

	        String response = authService.register(request);

	        return ResponseEntity.ok(response);
	    }
	    
	    @PostMapping("/login")
	    public ResponseEntity<LoginResponse> login(
	            @RequestBody LoginRequest request) {

	        LoginResponse response = authService.login(request);

	        return ResponseEntity.ok(response);
	    }
	
}
