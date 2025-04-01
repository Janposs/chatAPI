package com.example.chat.message;


import java.time.Instant;

import com.example.chat.chat.Chat;
import com.example.chat.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;


import jakarta.persistence.ManyToOne;


 
@Entity(name="message_")
public class Message {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(nullable=false)
	private int messageId;	

	private String content;

	@ManyToOne
	@JoinColumn(name="userId", nullable=false)
	private User sender;

	@ManyToOne
	@JoinColumn(name="chatId", nullable=false)
	private Chat destination;

	private Instant timestamp;

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}	

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public Chat getDestination() {
		return destination;
	}

	public void setDestination(Chat destinations) {
		this.destination = destinations;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public Message() {
	}

}
