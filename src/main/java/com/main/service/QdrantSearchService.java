package com.main.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Service;

import com.main.dto.SearchResult;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.WithPayloadSelectorFactory;
import io.qdrant.client.grpc.Points.ScoredPoint;
import io.qdrant.client.grpc.Points.SearchPoints;
import io.qdrant.client.grpc.Common.Filter;
import io.qdrant.client.grpc.JsonWithInt.Value;
import static io.qdrant.client.ConditionFactory.matchKeyword;

@Service
public class QdrantSearchService {
	
	private QdrantClient qdrantClient;
	private GeminiEmbeddingService geminiEmbeddingService;
	
	@org.springframework.beans.factory.annotation.Value("${qdrant.collection.name}")
	private String collectionName;
	
	public QdrantSearchService(QdrantClient qdrantClient, GeminiEmbeddingService geminiEmbeddingService) {
		
		this.qdrantClient=qdrantClient;
				this.geminiEmbeddingService=geminiEmbeddingService;
	}
	
	public List<SearchResult> search(String question, int limit, String documentId){
		
		try {
			
			//Convert question into embedding(vector)
			List<Float> embedding=geminiEmbeddingService.generateEmbedding(question);
			
		
			//Search Qdrant
			SearchPoints searchPoint= SearchPoints.newBuilder()
					.setCollectionName(collectionName) //NAME OF QDRANT COLLECTION
					.addAllVector(embedding) //VECTOR EMBEDDING
					.setLimit(limit) //MAXIMUM NUMBER OF MATCH RESULT
					.setFilter(Filter.newBuilder()
										.addMust(
												matchKeyword("documentId",documentId)
												)
										.build())//Only return chunks whose documentId matches this ID.
					.setWithPayload(WithPayloadSelectorFactory.enable(true))
					.build();
			
			List<ScoredPoint> points=qdrantClient.searchAsync(searchPoint).get();
			
			
			//Convert Qdrant results
			List<SearchResult> results= new ArrayList<>();
			
			for(ScoredPoint point: points) {
				
				//Get payload
				 Map<String, Value > payload =
	                        point.getPayloadMap();

	                System.out.println(
	                        "QDRANT PAYLOAD = " + payload
	                );

	                // Read payload fields
	                String text =payload.containsKey("text")? 
	                										payload.get("text").getStringValue(): "";

	                String fileName =payload.containsKey("fileName")? 
	                										payload.get("fileName").getStringValue(): "";

	                String payloadDocumentId =payload.containsKey("documentId")? 
	                										payload.get("documentId").getStringValue(): "";

	                int chunkIndex =payload.containsKey("chunkIndex")? 
	                												(int) payload.get("chunkIndex").getIntegerValue(): 0;
				
				results.add(new SearchResult(text,fileName,payloadDocumentId,chunkIndex,point.getScore()
						)
						);
			}
			return results;
		}
		catch(Exception e) {
			throw new RuntimeException("Qdrant search failed", e);
		}
		
		
	}
}
