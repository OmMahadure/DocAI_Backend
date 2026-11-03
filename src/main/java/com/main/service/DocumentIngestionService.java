package com.main.service;

import java.io.InputStream;
import java.util.List;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.main.entity.Document;
import com.main.entity.User;
//
//TIKA -> CHUNKING -> GEMINI -> QDRANT
//
@Service
public class DocumentIngestionService {
	
	private ChunkingService chunkingService;
	private GeminiEmbeddingService geminiEmbeddingService;
	private QdrantService qdrantService;
	private DocumentService documentService;
	
	private Tika tika=new Tika();
	
	public DocumentIngestionService(ChunkingService chunkingService, GeminiEmbeddingService geminiEmbeddingService, QdrantService qdrantService,DocumentService documentService) {
		this.chunkingService = chunkingService;
		this.geminiEmbeddingService = geminiEmbeddingService;
		this.qdrantService = qdrantService;
		this.documentService=documentService;
	}
	
	public int processDocument(MultipartFile file, User user){
		
		try {
			
			//Save file metadata in database
			Document document= documentService.saveDocument(file, user);
			
			//Use MySQL document ID as reference for Qdrant
			String documentID=String.valueOf(document.getId());
			
			System.out.println("MySQL Document ID: "+documentID);
			
			
			//Extract Text using Apache tika
			String text;
			
			try(InputStream inputStream = file.getInputStream()){
				text= tika.parseToString(inputStream);
			}
			
			if(text== null || text.isBlank()) {
				
				throw new RuntimeException("No text could be extracted from the document");
			}
						
			
			//Split document into chunks
			List<String> chunks=chunkingService.createChunks(text);
						
			System.out.println("Total chunks:" +chunks.size());
			
			// Generate embedding + store in Qdrant
			for(int i=0; i<chunks.size();i++) {
				
				String chunk=chunks.get(i);
				
				System.out.println("Processing chunk"+ (i+1)+ "/" + chunks.size());
				
				
				 // STEP 1: Gemini embedding
			    System.out.println("Calling Gemini embedding API...");
			    
				//Gemini embedding
				List<Float> embedding= geminiEmbeddingService.generateEmbedding(chunk);
				
				 System.out.println(
					        "Gemini embedding generated. Vector size: "
					        + embedding.size());
				
				 
				// STEP 2: Qdrant insertion
				    System.out.println("Sending vector to Qdrant...");
				    
				//Store vector + metadata
				qdrantService.saveChunk(
						documentID, 
						file.getOriginalFilename(), 
						i, 
						chunk, 
						embedding);
				
				 System.out.println(
					        "Qdrant insertion successful for chunk " + (i + 1)
					    );
				
			}
			return chunks.size();
			
		}catch(Exception e) {
			throw new RuntimeException(
					"Document ingestion failed", e);
		}
		
	}
}

