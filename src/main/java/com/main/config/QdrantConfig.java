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
	 
	 @Bean
	 public QdrantClient qdrantClient() {
		 
		 return new QdrantClient(
				 QdrantGrpcClient
				 .newBuilder(host,grpcPort, false)
				 .build()
				 );
		 
	 }
}
