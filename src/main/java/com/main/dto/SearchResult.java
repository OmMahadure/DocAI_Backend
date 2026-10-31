package com.main.dto;

public class SearchResult {
		
	private String text;
	private String fileName;
	private String documentId;
	private int chunkIndex;
	private float score;
	
	
	public SearchResult(String text, String fileName, String documentId, int chunkIndex, float score) {
		super();
		this.text = text;
		this.fileName = fileName;
		this.documentId = documentId;
		this.chunkIndex = chunkIndex;
		this.score = score;
	}


	public String getText() {
		return text;
	}


	public String getFileName() {
		return fileName;
	}


	public String getDocumentId() {
		return documentId;
	}


	public int getChunkIndex() {
		return chunkIndex;
	}


	public float getScore() {
		return score;
	}
	
	
	
	
	
}
