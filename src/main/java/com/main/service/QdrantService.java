package com.main.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Common.Filter;
import io.qdrant.client.grpc.Points.DeletePoints;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.PointsSelector;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;
import static io.qdrant.client.ConditionFactory.matchKeyword;


@Service
public class QdrantService {
		
		private QdrantClient qdrantClient;
		
		@Value("${qdrant.collection.name}")
		private String collectionName;
		
		public QdrantService(QdrantClient qdrantClient) {
			this.qdrantClient=qdrantClient;
			
		}
		
		public void saveChunk(
				String  documentId,
				String fileName,
				int chunkIndex,
				String text,
				List<Float> embedding) {
			
			  float[] vector = new float[embedding.size()];

		        for (int i = 0; i < embedding.size(); i++) {
		            vector[i] = embedding.get(i);
		        }

		        UUID pointUuid = UUID.nameUUIDFromBytes((documentId + "-" + chunkIndex)
		                        .getBytes()
		        );

		        PointStruct point = PointStruct.newBuilder()
		        								.setId(id(pointUuid))

		        								.setVectors(vectors(vector))

		        								.putAllPayload( Map.of
		        										("documentId",value(documentId),
		        										"fileName",value(fileName),
		        										"chunkIndex",value(chunkIndex),
		        										"text",value(text))
		        										)
		        								.build();

		        try {

		            qdrantClient
		                    .upsertAsync(
		                            collectionName,
		                            List.of(point)
		                    )
		                    .get();

		        } catch (Exception e) {

		            throw new RuntimeException(
		                    "Failed to insert chunk into Qdrant",
		                    e
		            );
		        }
		    }
		public String deleteDocumentChunks(String documentId) {
		
			    try {
			        // 1. Build the filter for matching documentId
			        Filter filter = Filter.newBuilder()
			                .addMust(matchKeyword("documentId", documentId))
			                .build();

			        // 2. Check if points with this documentId actually exist
			        long pointsCount = qdrantClient.countAsync(collectionName,filter,true).get();

			        if (pointsCount == 0) {
			            return "Chunks for documentId '" + documentId + "' do not exist in Qdrant.";
			        }

			        // 3. Build and execute delete request
			        DeletePoints deletePoints = DeletePoints.newBuilder()
			                .setCollectionName(collectionName)
			                .setPoints(PointsSelector.newBuilder().setFilter(filter).build())
			                .build();

			        qdrantClient.deleteAsync(deletePoints).get();

			        System.out.println("Chunks deleted for documentId: " + documentId);
			        return "Successfully deleted " + pointsCount + " chunk(s) for documentId: " + documentId;

			    } catch (Exception e) {
			        throw new RuntimeException("Failed to delete document chunks from Qdrant for ID: " + documentId, e);
			    }
			}
}
