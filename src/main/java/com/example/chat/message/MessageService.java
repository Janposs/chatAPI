package com.example.chat.message;

import java.time.Instant;

import com.example.chat.UnknownException;
import com.example.chat.chat.ChatService;
import com.example.chat.user.UserService;
import com.example.chat.PermissionException;

import org.springframework.stereotype.Service;

@Service
public class MessageService {
	private MessageRepository messageRepo;
	private UserService userService;
	private ChatService chatService;

	public MessageService(MessageRepository messageRepo, UserService userService, ChatService chatService) {
		this.messageRepo = messageRepo;
		this.userService = userService;
		this.chatService = chatService;
	}

	//check if user is the sender of message, and check if user is member of chat
	public void validateMessage(int userId, MessageRecord msg) {
		if (userId != msg.senderId()) {
			throw new PermissionException();
		}

		chatService.isMember(userId, msg.destinationId());
	}

	//TODO: how to do encryption? probably client side. maybe some flag so the server knows the message is not encrypted so it can be encrypted server side
	public String sendMessage(int senderId, MessageRecord message) {
		validateMessage(senderId, message);
		Message msg = new Message();
		msg.setContent(message.content());
		msg.setSender(userService.getUser(senderId));
		//I don't want to use the message timestamp.
		//I would have to validate it or something, and I don't want to do that. So I use Server time.
		msg.setTimestamp(Instant.now());
		//this should be done with just the chatService.
		msg.setDestination(chatService.getChat(message.destinationId()));
		messageRepo.save(msg);
		return new MessageRecord(msg).toString();
	}

	public Message getMessage(int messageId) {
		var msg = messageRepo.findById(messageId);
		if (msg.isEmpty()) {
			throw new UnknownException();
		}

		return msg.get();
	}

	public void deleteMessage(int userId,  int messageId) {
		var m = messageRepo.findById(messageId);
		if (m.isEmpty()) {
			throw new UnknownException();
		}
		
		if (userId != m.get().getSender().getUserId()) {
			throw new PermissionException();
		}

		//TODO: should I be able to delete a message if I am the sender but are not longer in the chat?
		
		messageRepo.deleteById(messageId);
	}

	public void edit(int userId, MessageRecord message) {
		validateMessage(userId, message);
		var m = messageRepo.findById(message.messageId());
		if (m.isEmpty()) {
			throw new UnknownException();
		}

		m.get().setContent(message.content());
		messageRepo.save(m.get());
	}

	public MessageRecord createMessageRecord(int senderId, String content, Instant timestamp, int destinationId) {
		return new MessageRecord(senderId, content, timestamp, destinationId);
	}
}
