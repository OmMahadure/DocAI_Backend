package com.main.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ChunkingService {

	private static int CHUNK_SIZE=4000;
	private static int OVERLAP =500;
	
	public List<String> createChunks(String text){
		
		List<String> chunks =new ArrayList<>();
		
		
		if(text== null || text.isBlank()) {
			return chunks;
		}
		
		int start=0;
		
		while(start < text.length()){
			
			int end=Math.min(
					start+CHUNK_SIZE,
					text.length()
		
		);
			
			String chunk=text.substring(start,end);
			
			chunks.add(chunk);
			
			if(end == text.length()) {
				break;
			}
			
			start= end-OVERLAP;
		}
		
		return chunks;
		
	}
}
