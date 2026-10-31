package com.main.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.main.entity.Document;
import com.main.entity.User;
import com.main.repository.UserRepo;
import com.main.service.ChunkingService;
import com.main.service.DocProcessingService;
import com.main.service.DocumentIngestionService;
import com.main.service.DocumentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins="http://localhost:5173")
public class DocumentController {

	private DocumentService documentService;
	
	private DocumentIngestionService documentIngestionService;
	
	private DocProcessingService docProcessingService;
	
	private ChunkingService chunkingService;
	
	private UserRepo userRepo;
	
	public DocumentController(DocumentService documentService, DocProcessingService docProcessingService, ChunkingService chunkingService, UserRepo userRepo, DocumentIngestionService documentIngestionService) {
		
		this.documentService=documentService;
		this.docProcessingService=docProcessingService;
		this.chunkingService=chunkingService;
		this.userRepo=userRepo;
		this.documentIngestionService=documentIngestionService;
	}
	
	@GetMapping("")
	public ResponseEntity<?> getAllDocuments(Authentication authentication) {
	    try {
	        User user = userRepo.findByEmail(authentication.getName())
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        List<Document> documents =
	                documentService.getAllDocuments(user.getId());

	        return ResponseEntity.ok(documents);

	    } catch (Exception e) {
	        return ResponseEntity.internalServerError()
	                .body("Error loading documents: " + e.getMessage());
	    }
	}
	
	@PostMapping("/upload")
	public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file, Authentication authentication) {
	
	try {
		
		if(file.isEmpty()) {
			
			return ResponseEntity.badRequest().body(
													Map.of("error", "Pleaseupload file")
													);
		}
		User user= userRepo.findByEmail(authentication.getName())
																.orElseThrow(()-> new RuntimeException("User not found."));
		
		
		int chunks= documentIngestionService.processDocument(file,user);
		
return ResponseEntity.ok(
				
				Map.of(
						"message","Document processed successfully",
						"fileName",file.getOriginalFilename(),
						"chunks",chunks
		)
		);
	}
	catch(Exception e) {
		return ResponseEntity.internalServerError().body("Error uploading file: "+e.getMessage());
	}
	
	}
	
	@PostMapping("/extract-text")
	public ResponseEntity<?> extractText(@RequestParam("file") MultipartFile file){
		
		try {
			
			String text= docProcessingService.extractText(file);
			
			return ResponseEntity.ok(text);
		}catch(Exception e){
			return ResponseEntity.internalServerError().body("Text extraction failed");
		}
	}
	
	@PostMapping("/chunks")
	public ResponseEntity<?> createChunks(@RequestParam("file") MultipartFile file){
		
		try {
			
			String text=docProcessingService.extractText(file);
			
			List<String> chunks= chunkingService.createChunks(text);
			
			return ResponseEntity.ok(chunks);
		}
		catch(Exception e) {
			
			e.printStackTrace();
			
			return ResponseEntity.internalServerError()
					.body("Chunking failed:"+ e.getMessage());
		}
		
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteDocument(@PathVariable Long id,  Authentication authentication){
		
		User user = userRepo.findByEmail(authentication.getName())
	            .orElseThrow(() -> new RuntimeException("User not found"));
		
		documentService.deleteDocument(id,user.getId());
		
		return ResponseEntity.ok(Map.of("message","document deleted successfully"));
	}
	
	
}

//POST /api/documents/upload
//		|
//		v
//Validate uploaded file
//		|
//		v
//Identify authenticated user
//		|
//		v
//DocumentIngestionService
//		|
//		v
//Save document + user ownership
//		|
//		v
//Extract text using Apache Tika
//		|
//		v
//Split text into chunks
//		|
//		v
//Generate Gemini embeddings
//		|
//		v
//Store vectors and metadata in Qdrant
//		|
//		v
//Return success response
	
	/*@PostMapping("/upload")
	public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file) {
		
		if(file.isEmpty()) {
			return ResponseEntity.badRequest().body("File can not be empty");
			
		}
		Document document =new Document();
		
		document.setFileName(file.getOriginalFilename());
		document.setFileType(file.getContentType());
		document.setFileSize(file.getSize());
		
		Document saveDocument=documentService.saveDocument(document);
		
		return ResponseEntity.ok(saveDocument);
	}*/
