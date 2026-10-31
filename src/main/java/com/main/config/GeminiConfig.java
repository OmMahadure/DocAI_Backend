package com.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.util.Value;
import com.google.genai.Client;

@Configuration
public class GeminiConfig {
	
	
	@Bean
	public Client geminiClient() {
		
		return new Client();
	}
	
	
}
