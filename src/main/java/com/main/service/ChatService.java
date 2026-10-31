package com.main.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.main.dto.SearchResult;
import com.main.entity.ChatMessage;
import com.main.entity.Document;
import com.main.repository.ChatMessageRepo;
import com.main.repository.DocumentRepo;

import jakarta.transaction.Transactional;

@Service
public class ChatService {

		private ChatMessageRepo chatMessageRepo;
		private QdrantSearchService qdrantSearchService;
		private RAGAnswerService ragAnswerService;
		private DocumentRepo documentRepo;
		
		
		public ChatService(ChatMessageRepo chatMessageRepo, QdrantSearchService qdrantSearchService, RAGAnswerService ragAnswerService,DocumentRepo documentRepo) {
			this.chatMessageRepo = chatMessageRepo;
			this.qdrantSearchService = qdrantSearchService;
			this.ragAnswerService = ragAnswerService;
			this.documentRepo=documentRepo;
		}
		
		
		public String ask(Long userId,Long documentId, String question){
			
			 // Verify that the document belongs to the authenticated user.
		    Document document = documentRepo.findById(documentId)
		            .orElseThrow(()-> new RuntimeException ("Document not found")) ;
		    
		    if (document.getUser() == null
		            || !document.getUser().getId().equals(userId)) {
		        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this document");
		    }
		    
		    long totalStart = System.currentTimeMillis();

		    long searchStart = System.currentTimeMillis();
		    
		 // Retrieve relevant chunks only from the selected document
			List<SearchResult> results=qdrantSearchService.search(question,5,String.valueOf(documentId));
			
			System.out.println(
				    "Qdrant search: " + (System.currentTimeMillis() - searchStart) + " ms"
				);
			
			long geminiStart = System.currentTimeMillis();
			//Generate answer
			String answer= ragAnswerService.generateAnswer(question, results);
			
			System.out.println(
				    "Gemini generation: " +
				    (System.currentTimeMillis() - geminiStart) + " ms"
				);
			
			long saveStart = System.currentTimeMillis();
			//Save conversation
			ChatMessage message= new ChatMessage();
			message.setUserId(userId);
			message.setDocumentId(documentId);
			message.setQuestion(question);
			message.setAnswer(answer);
			
			chatMessageRepo.save(message);
			
			System.out.println(
				    "MySQL save: " +
				    (System.currentTimeMillis() - saveStart) + " ms"
				);
			
			System.out.println(
				    "Total service time: " +
				    (System.currentTimeMillis() - totalStart) + " ms"
				);
			
			return answer;
			
		}
		
		
		@Transactional
		public void clearDocumentHistory(Long userId, Long documentId){
			 // Verify that the document belongs to the authenticated user
		    Document document = documentRepo.findById(documentId)
		            .orElseThrow(() -> new ResponseStatusException(
		                    HttpStatus.NOT_FOUND, "Document not found"));
		    
		    if (document.getUser() == null
		            || !document.getUser().getId().equals(userId)) {
		        throw new ResponseStatusException(
		                HttpStatus.FORBIDDEN, "Access denied to this document");
		    }
		    
			 chatMessageRepo.deleteByUserIdAndDocumentId(
			             userId,  documentId);
		}
		
		
		public List<ChatMessage> getDocumentHistory(Long userId,Long documentId) {
			  // Verify document ownership before returning chat history.
		    Document document = documentRepo.findById(documentId)
		            .orElseThrow(() -> new ResponseStatusException(
		                    HttpStatus.NOT_FOUND, "Document not found"));

		    if (document.getUser() == null
		            || !document.getUser().getId().equals(userId)) {
		        throw new ResponseStatusException(
		                HttpStatus.FORBIDDEN, "Access denied to this document");
		    }

		    return chatMessageRepo
		            .findByUserIdAndDocumentIdOrderByIdAsc(userId, documentId);		
		}
}
