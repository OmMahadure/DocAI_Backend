
package com.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Collections.PayloadSchemaType;

@Configuration
public class QdrantCollectionConfig {

    @Value("${qdrant.collection.name}")
    private String collectionName;

    @Value("${qdrant.vector.size}")
    private int vectorSize;

    @Bean
    public CommandLineRunner createQdrantCollection(
            QdrantClient qdrantClient) {

        return args -> {

            // Step 1: Create collection if it doesn't exist
            try {

                qdrantClient.createCollectionAsync(
                        collectionName,
                        VectorParams.newBuilder()
                                .setSize(vectorSize)
                                .setDistance(Distance.Cosine)
                                .build()
                ).get();

                System.out.println(
                        "Qdrant collection created: " + collectionName
                );

            } catch (Exception e) {

                if (e.getMessage() != null &&
                    e.getMessage().contains("already exists")) {

                    System.out.println(
                            "Qdrant collection already exists: "
                            + collectionName
                    );

                } else {
                    throw e;
                }
            }

            // Step 2: Create documentId payload index
            try {

                qdrantClient.createPayloadIndexAsync(
                        collectionName,
                        "documentId",
                                PayloadSchemaType.Keyword,
                                null,
                                true,
                                null,
                                null
                ).get();

                System.out.println(
                        "Qdrant documentId payload index created successfully."
                );

            } catch (Exception e) {

                System.err.println(
                        "Failed to create documentId payload index: "
                        + e.getMessage()
                );

                throw e;
            }
        };
    }
}
