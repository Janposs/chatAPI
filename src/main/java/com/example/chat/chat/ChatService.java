package com.example.chat.chat;

import java.util.ArrayList;

import com.example.chat.PermissionException;
import com.example.chat.UnknownException;
import com.example.chat.user.User;
import com.example.chat.user.UserRecord;
import com.example.chat.user.UserService;
import com.example.chat.message.MessageRecord;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ChatService {
	private ChatRepository chatRepo;
	private UserService userService;

	public ChatService(ChatRepository chatRepo, UserService userService) {
		this.chatRepo = chatRepo;
		this.userService = userService;
	}

	@Transactional
	public Chat getChat(int chatId) {
		var chat = chatRepo.findById(chatId);
		if (chat.isEmpty()) {
			throw new UnknownException();
		}

		return chat.get();
	}
	@Transactional
	public ArrayList<MessageRecord> getMessages(int userId, int chatId, int max) {
		isMember(userId, chatId);
		Chat c = getChat(chatId);
		//reverse list to return the newest message at first position
	    var m = c.getMessages().reversed();
		ArrayList<MessageRecord> ret = new ArrayList<>();
		if (max >= m.size()) {
			//return the whole list reversed
			m.forEach(msg -> ret.add(new MessageRecord(msg)));
			return ret;
		}
		
		for (int i = 0; i < max; i++) {
			ret.add(new MessageRecord(m.get(i)));
		}

		return ret;
		
	}
	@Transactional
	public ChatRecord createChat(int ownerId, String title, String description) {
		Chat chat = new Chat();
		User user = userService.getUser(ownerId);
		
		chat.setOwner(user);
		chat.setDescription(description);
		chat.setTitle(title);
		ArrayList<User> members = new ArrayList<>();
		members.add(user);
		chat.setMembers(members);
		return new ChatRecord(chatRepo.save(chat));
		
	}

	@Transactional
	public void deleteChat(int ownerId, int chatId) {
		if (!isOwner(ownerId, chatId)) {
			throw new PermissionException();
		}
		//do I delete all the messages too?
		chatRepo.delete(getChat(chatId));
	}
	@Transactional
	public void addUser(int ownerId, int chatId, String user) {
		if (!isOwner(ownerId, chatId)) {
			throw new PermissionException();
		}

		var chat = chatRepo.findById(chatId);
		if (chat.isEmpty()) {
			throw new UnknownException();
		}

		chat.get().getMembers().addLast(userService.getByName(user));
		chatRepo.save(chat.get());
	}
	
	@Transactional
	public void removeUser(int ownerId, int chatId, UserRecord user) {
		if (!isOwner(ownerId, chatId)) {
			throw new PermissionException();
		}
		
		int userId = userService.getByName(user.username()).getUserId();
		if (ownerId == userId) {
			//owner should not be able to remove themselfs
			throw new UnknownException();
		}

		isMember(userId, chatId);
		var chat = chatRepo.findById(chatId);
		var members = chat.get().getMembers();
		for (int i = 0; i < members.size(); i++) {
			if (members.get(i).getUserId() == userId) {
				members.remove(i);
				chatRepo.save(chat.get());
				return;
			}
		}
	}

	//TODO: this is a temporary implementation. I want to have multiple owners for a chat.n
	@Transactional
	public void changeOwner(int ownerId, int chatId, UserRecord newOwner) {
		if (!isOwner(ownerId, chatId)) {
			throw new PermissionException();
		}
		User no = userService.getByName(newOwner.username());
		//TODO: some consistency things isOwner returns a boolean and this throws an exception if false.
		//only one pattern should be used. Not sure which one.
		isMember(no.getUserId(), chatId);
		Chat c = getChat(chatId);
		c.setOwner(no);
		chatRepo.save(c);
	}
	
	@Transactional
	//I just make this separate from remove
	public void leaveChat(int userId, int chatId) {
		var chat = chatRepo.findById(chatId);
		if (chat.isEmpty()) {
			throw new UnknownException();
		}
		
		isMember(userId, chatId);
		if (isOwner(userId, chatId)) {
			//owner can not leave chat without setting new owner.
			throw new UnknownException();
		}
		
		var members = chat.get().getMembers();
		for (int i = 0; i < members.size(); i++) {
			if (members.get(i).getUserId() == userId) {
				members.remove(i);
				chatRepo.save(chat.get());
				return;
			}
		}
	}
	
	@Transactional
	public void setTitle(int ownerId, int chatId, String title) {
		if (!isOwner(ownerId, chatId)) {
			throw new PermissionException();
		}
		//I don't need to check if the chat exists. Is done by code above.
		var chat = chatRepo.findById(chatId);
		chat.get().setTitle(title);
	}
	@Transactional
	public void setDescription(int ownerId, int chatId, String des) {
		if (!isOwner(ownerId, chatId)) {
			throw new PermissionException();
		}
		var chat = chatRepo.findById(chatId);
		chat.get().setDescription(des);
	}
	
	@Transactional
	public void isMember(int userId, int chatId) {
		var chat = chatRepo.findById(chatId);
		if (chat.isEmpty()) {
			throw new UnknownException();
		}

		if (!chat.get().getMembers().contains(userService.getUser(userId))) {
			throw new PermissionException();
		}
	}

	//check if user is owner of chat
	//the other way of doing this would be to get all owned chats of the user and iterate over the list
	//and check each chat.
	@Transactional
	public boolean isOwner(int userId, int chatId) {
		var chat = chatRepo.findById(chatId);
		if (chat.isEmpty()) {
			throw new UnknownException();
		}

		if (chat.get().getOwner().getUserId() != userId) {
			return false;
		}
		
		return true;
	}
	
	@Transactional
	public ArrayList<UserRecord> getMembers(int userId, int chatId) {
		isMember(userId, chatId);
		var c = getChat(chatId);
		ArrayList<UserRecord> ret = new ArrayList<>();
		c.getMembers().forEach(m -> ret.addLast(new UserRecord(m)));
		return ret;
	}
}
