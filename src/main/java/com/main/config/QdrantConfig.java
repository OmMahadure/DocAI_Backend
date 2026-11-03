package com.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Value;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;

@Configuration
public class QdrantConfig {
	
	 @Value("${qdrant.host}")
	 private String host;
	 
	 @Value("${qdrant.grpc.port}")
	 private int grpcPort;
	 
	 @Value("${qdrant.api.key:}")
	    private String apiKey;
	 
	 @Value("${qdrant.use.tls:false}")
	    private boolean useTls;
	 
	 @Bean
	 public QdrantClient qdrantClient() {
		 
		 return new QdrantClient(
				 QdrantGrpcClient
				 .newBuilder(host,grpcPort, useTls)
				 .build()
				 );
		 
	 }
}
