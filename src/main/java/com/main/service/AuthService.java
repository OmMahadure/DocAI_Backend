package com.main.service;


import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.main.dto.LoginRequest;
import com.main.dto.LoginResponse;
import com.main.dto.RegisterRequest;
import com.main.entity.User;
import com.main.repository.UserRepo;

@Service
public class AuthService {
	
	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;
	
	private final JWTService jwtService;
	public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder,JWTService jwtService) {
		super();
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
		this.jwtService=jwtService;
	}
	
	public String register(RegisterRequest request) {
		
		if(userRepo.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already registered");
			
		}
		
		User user=new User();
		user.setName(request.getName());
		user.setEmial(request.getEmail().toLowerCase());
		user.setPassword(
				 passwordEncoder.encode(request.getPassword())
				 );
		userRepo.save(user);
		
		return "User registered successfull";
	}
	
	public LoginResponse login(LoginRequest request) {
		 String email = request.getEmail()
		            .trim()
		            .toLowerCase(Locale.ROOT);

		    User user = userRepo.findByEmail(email)
		            .orElseThrow(() ->
		                    new RuntimeException("Invalid email or password"));

		    if (!passwordEncoder.matches(
		            request.getPassword(), user.getPassword())) {

		        throw new RuntimeException("Invalid email or password");
		    }
		    
		    String token=jwtService.generateToken(user.getId(), user.getEmial());
		    
		    return new LoginResponse(
		            user.getId(),
		            user.getName(),
		            user.getEmial(),
		            token
		    );
		}
}
