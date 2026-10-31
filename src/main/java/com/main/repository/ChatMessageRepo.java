package com.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.entity.ChatMessage;

public interface ChatMessageRepo extends JpaRepository<ChatMessage, Long> {

	  void deleteByUserIdAndDocumentId(
	            Long userId, Long documentId);
	
	List<ChatMessage> findByUserIdAndDocumentIdOrderByIdAsc(Long userId, Long documentId);
}
