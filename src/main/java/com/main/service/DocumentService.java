package com.main.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
	//Uploads is the folder where the files are saved in the project directory.
	private String uploadDirectory="uploads/";
	
	
	public Document saveDocument(MultipartFile file, User user) throws IOException {
		
		Path uploadPath= Paths.get(uploadDirectory);
		
		
		if(!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		
		String originalfileName=file.getOriginalFilename();
		
		String uuid = UUID.randomUUID().toString().substring(0, 8);
		String storedFileName=uuid+"_"+originalfileName;
		
		Path filePath=uploadPath.resolve(storedFileName);
		Files.copy(file.getInputStream(),filePath);
		
		Document document= new Document();
		
		document.setFileName(originalfileName);
		document.setStoredFileName(storedFileName);
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
		
		//Delete physical file (/Uploads folder)
		
		String fileNameToDelete = document.getStoredFileName() != null 
                ? document.getStoredFileName() 
                : document.getFileName();

        if (fileNameToDelete != null) {
		Path filePath=Paths.get(uploadDirectory)
										.resolve(fileNameToDelete);
		
		Files.deleteIfExists(filePath);
		System.out.println("File deleted: "+filePath.toString());
        }
        
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
