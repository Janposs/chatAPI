package com.example.chat.chat;

public record ChatRecord(String title, String description, int chatId) {
	public ChatRecord(String title, String description) {
		this(title, description, 0);
	}

	public ChatRecord(Chat chat) {
		this(chat.getTitle(), chat.getDescription(), chat.getChatId());
	}
}
