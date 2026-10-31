package com.main.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.main.dto.SearchResult;
import com.main.entity.Document;
import com.main.service.DocumentService;
import com.main.service.QdrantSearchService;

@RestController
@RequestMapping("/api/search")
public class SearchController {
		
		private QdrantSearchService qdrantSearchService;
		private DocumentService documentService;
		public SearchController(QdrantSearchService qdrantSearchService, DocumentService documentService) {
			
			this.qdrantSearchService = qdrantSearchService;
				this.documentService=documentService;
		}
		
		@GetMapping
		public ResponseEntity<?> search(@RequestParam String query){
			
			Document latestDocument=documentService.getLatestDocument();
			if(latestDocument==null) {
				return ResponseEntity.badRequest().body(
															Map.of(
																	"error","No document found. Please upload a document first."));
			}
			if(query== null || query.isBlank()) {
				
				return ResponseEntity.badRequest().body(
															Map.of(
																	"error","Query cannot be empty"));
			}
			
			String documentId=String.valueOf(latestDocument.getId());
			
			List<SearchResult> results=qdrantSearchService.search(query, 5,documentId);
			
			
			return ResponseEntity.ok(results);
		}
}
