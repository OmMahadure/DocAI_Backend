package com.main.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="chat_messages")
@Getter
@Setter
@JsonPropertyOrder({"id", "userId","documentId", "question", "answer"})
public class ChatMessage {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	private Long userId;
	
	@Column(nullable = false)
	private Long documentId;
	
	@Column(columnDefinition = "TEXT")
	private String question;
	
	@Column(columnDefinition = "TEXT")
	private String answer;
	
	

}
