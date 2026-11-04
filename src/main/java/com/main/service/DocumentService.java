package com.main.service;

import java.io.IOException;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.main.entity.Document;
import com.main.entity.User;
import com.main.repository.DocumentRepo;

@Service
public class DocumentService {
	
	private DocumentRepo documnetRepo;
	
	QdrantService qdrantService;
	
	public DocumentService(DocumentRepo documnetRepo, 	QdrantService qdrantService) {
		this.documnetRepo=documnetRepo;
		this.qdrantService=qdrantService;
		
	}
	
	
	public Document saveDocument(MultipartFile file, User user) throws IOException {
	
		
		Document document= new Document();
		
		document.setFileName(file.getOriginalFilename());
		document.setFileSize(file.getSize());
		document.setFileType(file.getContentType());
		document.setStatus("UPLOADED");
		document.setUploadedAt(LocalDateTime.now());
		document.setUser(user);
		
		return documnetRepo.save(document);
	}
	
	
	
public List<Document> getAllDocuments(Long userId){
		
		return documnetRepo.findByUserId(userId);
	}

public Document getLatestDocument() {

    return documnetRepo
            .findTopByOrderByUploadedAtDesc()
            .orElse(null);
}


	
public void deleteDocument(Long id, Long userId) {
	try {
		
		Document document=documnetRepo.findByIdAndUserId(id, userId)
										.orElseThrow(()-> 
													new RuntimeException("Document not found")
													);
		
		String documentId=String.valueOf(document.getId());
		
		//Delete Qdrant chunks
		qdrantService.deleteDocumentChunks(documentId);
	
		//Delete MySql record
		documnetRepo.delete(document);
		System.out.println("Document record deleted with id: "+id);
		
	}catch(Exception e) {
		
		throw new RuntimeException("Document deletion failed",e);
	}
}
	
	/*
	 * public Document saveDocument(Document document) {
	 * 
	 * document.setStatus("UPLOADED"); document.setUploadedAt(LocalDateTime.now());
	 * 
	 * return documnetRepo.save(document); }
	 */
	
	
	
	
	
		
}
