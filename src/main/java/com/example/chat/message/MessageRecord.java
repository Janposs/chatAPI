package com.example.chat.message;

import java.time.Instant;

public record MessageRecord(int senderId, String content, Instant timestamp, int destinationId, int messageId) {
	public MessageRecord(int senderId, String content, Instant timestamp, int destinationId) {
		this(senderId, content, timestamp, destinationId, 0);
	}

	public MessageRecord(Message msg) {
		this(msg.getSender().getUserId(), msg.getContent(), msg.getTimestamp(), msg.getDestination().getChatId(),
				msg.getMessageId());
	}

	public MessageRecord(String content, int senderId, int destinationId) {
		this(senderId, content, null, destinationId, 0);
	}
}
