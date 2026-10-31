package com.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.entity.Document;

public interface DocumentRepo extends JpaRepository<Document, Long> {
		
	//This finds the document with the latest upload time
	Optional<Document> findTopByOrderByUploadedAtDesc();
	
	List<Document> findByUserId(Long userId);
	
	Optional<Document> findByIdAndUserId(Long id, Long userId);
}
