package com.main.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.main.service.GeminiEmbeddingService;

@RestController
@RequestMapping("/gemini")
public class GeminiController {
	
	private GeminiEmbeddingService embeddingService;
	
	public GeminiController(GeminiEmbeddingService embeddingService) {
		this.embeddingService=embeddingService;
	}
	
	@GetMapping("/embedding")
	public List<Float> testEmbedding(@RequestParam String text){
		
		return embeddingService.generateEmbedding(text);
		
	}
}
