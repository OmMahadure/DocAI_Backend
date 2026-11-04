package com.main.config;


import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.main.filter.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {
	
	private JwtAuthenticationFilter authenticationFilter;
	
	
	public SecurityConfig(JwtAuthenticationFilter authenticationFilter) {
		super();
		this.authenticationFilter = authenticationFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		
		http.cors(cors -> {})
			.csrf(csrf->csrf.disable()) //session tokens
			.formLogin(form-> form.disable())
			.httpBasic(basic->basic.disable())
			
			.sessionManagement(session->session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			
			.authorizeHttpRequests(auth-> auth //access control rules based on URL patterns
					.requestMatchers("/api/auth/register",
									 "/api/auth/login").permitAll()
														.anyRequest().authenticated())
			
			 .addFilterBefore(
					 authenticationFilter,
		                UsernamePasswordAuthenticationFilter.class
		            );
		return http.build();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();

	    configuration.setAllowedOrigins(
	        List.of("http://localhost:5173",
	        		"https://docai-document-assistant.vercel.app")
	    );

	    configuration.setAllowedMethods(
	        List.of("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS")
	    );

	    configuration.setAllowedHeaders(
	        List.of("Authorization", "Content-Type", "Accept","*")
	    );

	    configuration.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source =
	        new UrlBasedCorsConfigurationSource();

	    source.registerCorsConfiguration("/**", configuration);

	    return source;
	}
}
