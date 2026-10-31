package com.main.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.main.dto.SearchResult;
import com.main.entity.Document;
import com.main.service.DocumentService;
import com.main.service.QdrantSearchService;
import com.main.service.RAGAnswerService;

@RestController
@CrossOrigin(origins="http://localhost:5173")
public class RAGController {
		
	private QdrantSearchService qdrantSearchService;
	private DocumentService documentService;
	private RAGAnswerService ragAnswerService;
	
	public RAGController(QdrantSearchService qdrantSearchService, RAGAnswerService ragAnswerService, DocumentService documentService) {
		
		this.qdrantSearchService=qdrantSearchService;
		this.ragAnswerService=ragAnswerService;
		this.documentService=documentService;
	}
	@GetMapping("/api/ask")
	public String ask(@RequestParam String query) {
		
		Document latestDocument=documentService.getLatestDocument();
		if(latestDocument==null) {
			return "No document found. Please upload a document first.";
		}
		
		String documentId=String.valueOf(latestDocument.getId());
		//Retrieve relevant chunks
		List<SearchResult> results = qdrantSearchService.search(query, 5,documentId);
		
		//Generate answer using Gemini
		
		return ragAnswerService.generateAnswer(query, results);
	}
}
