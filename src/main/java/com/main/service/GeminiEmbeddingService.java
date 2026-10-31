package com.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;

@Service
public class GeminiEmbeddingService {

	private Client geminiClient;
	
	public GeminiEmbeddingService(Client geminiClient) {
		this.geminiClient=geminiClient;
		
	}
	
	public List<Float> generateEmbedding(String text){
		
		EmbedContentConfig config=EmbedContentConfig.builder()
				.outputDimensionality(768)
				.build();
		
		EmbedContentResponse response=geminiClient.models.embedContent("gemini-embedding-001",text,config);
	
	return response.embeddings()
			.orElseThrow(()->new RuntimeException("No embedding returned"))
			.get(0)
			.values()
			.orElseThrow(()->new RuntimeException("Embedding value are empty"));
	}
}
