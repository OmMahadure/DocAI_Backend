package com.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;

@Configuration
public class QdrantCollectionConfig {
	@Value("${qdrant.collection.name}")
		private String collectionName;
		
	@Value("${qdrant.vector.size}")
		private int vectorSize;
	
	@Bean
	public CommandLineRunner createQdrantCollection(QdrantClient qdrantClient) {
		
		return args-> {
			
			try {
				 qdrantClient
                 .createCollectionAsync(
                         collectionName,
                         VectorParams.newBuilder()
                                 .setSize(vectorSize)
                                 .setDistance(Distance.Cosine)
                                 .build()
                 )
                 .get();
				
			System.out.println("Qdrant collection created:"+collectionName);
				}catch(Exception e) {
					if(e.getMessage() !=null && e.getMessage().contains("already exists")) {
						
						System.out.println("Qdrant collection already exist:"+ collectionName);
					}
					else {
						throw e;
					}
				}
		};
		
	}
	
}
