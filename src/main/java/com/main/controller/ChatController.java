package com.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.main.entity.ChatMessage;
import com.main.entity.User;
import com.main.repository.UserRepo;
import com.main.service.ChatService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {
	
		private ChatService chatService;
		private UserRepo userRepo;

		public ChatController(ChatService chatService,UserRepo userRepo) {
			super();
			this.chatService = chatService;
			this.userRepo=userRepo;
		}
		
		private User getAuthenticatedUser(Authentication authentication) {
		    return userRepo.findByEmail(authentication.getName())
		            .orElseThrow(() ->
		                    new RuntimeException("Authenticated user not found"));
		}
		
		@PostMapping("/ask")
			public ResponseEntity<String> ask(@RequestParam Long documentId, @RequestParam String question, Authentication authentication) {
				
			User user=getAuthenticatedUser(authentication);
			
		
			  String answer = chatService.ask(
			            user.getId(), documentId, question);

			    return ResponseEntity.ok(answer);			
		}
		
		
		@DeleteMapping("/history/document/{documentId}")
		public ResponseEntity<?> clearChatHistory(
		        @PathVariable Long documentId, Authentication authentication) {
			
			 User user = getAuthenticatedUser(authentication);
			 
		    chatService.clearDocumentHistory(user.getId(),documentId);
		    
		    return ResponseEntity.noContent().build();
		}
		
		@GetMapping("/history/document/{documentId}")
		public  ResponseEntity<List<ChatMessage>>  documentHistory(
		        @PathVariable Long documentId,Authentication authentication) {

			
			User user = getAuthenticatedUser(authentication);

		    List<ChatMessage> history = chatService.getDocumentHistory(
		            user.getId(), documentId);

		    return ResponseEntity.ok(history);		}
}
