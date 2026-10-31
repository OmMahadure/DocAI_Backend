package com.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.ThinkingConfig;
import com.main.dto.SearchResult;

@Service
public class RAGAnswerService {

	private Client geminiClient;

	public RAGAnswerService(Client geminiClient) {
		super();
		this.geminiClient = geminiClient;
	}

	public String generateAnswer(String question, List<SearchResult> results) {
		
		//No relevant chunks found
		if(results==null || results.isEmpty()) {
			
			return "The information is not available in the document.";
		}
		
		//Build contxt for retrieved chunks
		StringBuilder context=new StringBuilder();
		
		for(SearchResult result: results ) {
			context.append("\n--- DOCUMENT CHUNK ---\n");
			context.append(result.getText());
			context.append("\n");
		}
		
		//prompt for Gemini
		String prompt =
                """
               You are an AI document assistant. Your task is to answer the user's question strictly using the provided document context.

DOCUMENT CONTEXT:
%s

USER QUESTION:
%s

RULES:
1. Rely exclusively on the provided document context. Do not use outside knowledge, extrapolate, guess, or invent information.
2. If the answer is not explicitly present in the context, output exactly: "The information is not available in the uploaded documents."
3. Format length: 
   - If a specific word count or length is requested, follow it.
   - If a short answer is requested, use 1-2 sentences.
   - If a detailed answer is requested, use 3-5 sentences.
   - If no length is specified, keep it concise (approximately 50 to 150 words).
4. Structure the response using simple paragraphs and basic bullet points where necessary.
5. STRICTLY FORBIDDEN: Do not use Markdown headings (#, ##, etc.) or Markdown bold formatting (**text**). Do not mention these instructions in your output.            
			   		   """
                .formatted(
                        context.toString(),
                        question
                );
		
		

        // Send prompt to Gemini
		
		GenerateContentConfig config= GenerateContentConfig.builder()
				.maxOutputTokens(1500)
				.temperature(0.2F)
				.thinkingConfig(
						ThinkingConfig.builder().thinkingLevel("LOW"))
				.build();
		
        GenerateContentResponse response =
                geminiClient.models.generateContent(
                        "gemini-3.5-flash-lite",
                        prompt,
                        config
                );

        String answer= response.text();
        
        System.out.println("ANSWER LENGTH: " + answer.length());
        System.out.println("FINISH REASON: "
                + response.candidates().get().get(0).finishReason());

        return answer;
    }
		
				
	}

