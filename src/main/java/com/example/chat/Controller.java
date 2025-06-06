package com.example.chat;

import java.util.ArrayList;

import com.example.chat.chat.ChatInvite;
import com.example.chat.chat.ChatRecord;
import com.example.chat.chat.ChatService;
import com.example.chat.message.MessageRecord;
import com.example.chat.message.MessageService;
import com.example.chat.user.LoginRequest;
import com.example.chat.user.RegisterRequest;
import com.example.chat.user.UserRecord;
import com.example.chat.user.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
//this should probably be more specific
//TODO: THIS OF COURSE SHOULD BE CHANGED
@CrossOrigin
public class Controller {
    private final ChatService chatService;
	private final UserService userService;
	private final MessageService messageService;

 Controller(ChatService chatService, UserService userService, MessageService messageService) {
        this.chatService = chatService;
		this.userService = userService;
		this.messageService = messageService;
    }
	//return all new things(messages chats other stuff)
	@GetMapping("/poll")
	public ResponseEntity<?> poll(@AuthenticationPrincipal Jwt jwt) {
		return null;
	}
	
	/*
	 * USER
	 */

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest user) {
		//what do I do I one of the strings is empty?
		userService.createUser(user.username(), user.password(), user.displayname());
		return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<Jwt> login(@RequestBody LoginRequest req) {
		return new ResponseEntity<>(userService.authenticate(req), HttpStatus.OK);
	}

	//return all users that have username as prefix
	@GetMapping("/user/find/{username}")
	public ResponseEntity<ArrayList<UserRecord>> findUser(@PathVariable String username) {
		//return OK even if the list is empty.
		return new ResponseEntity<>(userService.find(username), HttpStatus.OK);
	}

	//Later. needs a bit of work.
	@PostMapping("user/block")
	public ResponseEntity<?> blockUser(@AuthenticationPrincipal Jwt jwt) {
		return null;
	}

	//TODO: maybe add a contatct list
	
	/*
	 * CHAT
	 */
	@PostMapping("/chat/create")
	public ResponseEntity<ChatRecord> createChat(@AuthenticationPrincipal Jwt jwt, @RequestBody ChatRecord chat) {
		//this parse int thing needs changing. not even sure if I need it.
		return new ResponseEntity<>(chatService.createChat(Integer.parseInt(jwt.getSubject()), chat.title(), chat.description()), HttpStatus.CREATED);
	}

	@PostMapping("/chat/add")
	public ResponseEntity<Void> addUser(@AuthenticationPrincipal Jwt jwt, @RequestBody ChatInvite invite) {
		chatService.addUser(invite.chatOwnerId(), invite.chatId(), invite.user());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	//get all of a user's chats
	@GetMapping("/chat/chats")
	public ResponseEntity<ArrayList<ChatRecord>> getChats(@AuthenticationPrincipal Jwt jwt) {
		return new ResponseEntity<>(userService.getChats(Integer.parseInt(jwt.getSubject())), HttpStatus.OK);
	}

	//do I want to return something here?
	@DeleteMapping("/chat/delete/{id}")
	public ResponseEntity<Void> deleteChat(@AuthenticationPrincipal Jwt jwt, @PathVariable int id) {
		chatService.deleteChat(Integer.parseInt(jwt.getSubject()), id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	//get all members of a chat a user is in
	@GetMapping("/chat/member/{id}")
	public ResponseEntity<ArrayList<UserRecord>> getChatMember(@AuthenticationPrincipal Jwt jwt, @PathVariable int id) {
		return new ResponseEntity<>(chatService.getMembers(Integer.parseInt(jwt.getSubject()), id), HttpStatus.OK);
	}

	@PutMapping("/chat/leave/{id}")
	public ResponseEntity<Void> leaveChat(@AuthenticationPrincipal Jwt jwt, @PathVariable int id) {
		chatService.leaveChat(Integer.parseInt(jwt.getSubject()), id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/chat/change-owner/{id}")
	public ResponseEntity<Void> changeOwner(@AuthenticationPrincipal Jwt jwt, @PathVariable int id,
			UserRecord newOwner) {
		chatService.changeOwner(Integer.parseInt(jwt.getSubject()), id, newOwner);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/chat/remove/{id}")
	public ResponseEntity<Void> removeFromChat(@AuthenticationPrincipal Jwt jwt, @PathVariable int id, @RequestBody UserRecord user) {
		chatService.removeUser(Integer.parseInt(jwt.getSubject()), id, user);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	//return messages from chat. num is the max amount you want to receive. Can obviously return less if
	//num > chat.messages().size()
	//TODO: these methods probably don't work correctly.
	@GetMapping("/chat/messages/{chatId}/{max}")
	public ResponseEntity<ArrayList<MessageRecord>> getMessages(@AuthenticationPrincipal Jwt jwt, @PathVariable int chatId, @PathVariable int max) {
		return new ResponseEntity<>(chatService.getMessages(Integer.parseInt(jwt.getSubject()), chatId, max), HttpStatus.OK); 
	}

	/*
	 * MESSAGE
	 */
    @PostMapping("/message/send")
    public ResponseEntity<MessageRecord> sendMessage(@AuthenticationPrincipal Jwt jwt, @RequestBody MessageRecord message) {
		//not sure if I want to return the message record. probably yes.
		return new ResponseEntity<>(messageService.sendMessage(Integer.parseInt(jwt.getSubject()), message), HttpStatus.OK);
    }

	@DeleteMapping("/message/delete/{id}")
	public ResponseEntity<Void> deleteMessage(@AuthenticationPrincipal Jwt jwt, @PathVariable int messageId) {
		messageService.deleteMessage(Integer.parseInt(jwt.getSubject()), messageId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/message/edit")
	public ResponseEntity<MessageRecord> editMessage(@AuthenticationPrincipal Jwt jwt, MessageRecord message) {
		messageService.edit(Integer.parseInt(jwt.getSubject()), message);
		return new ResponseEntity<>(messageService.edit(Integer.parseInt(jwt.getSubject()), message), HttpStatus.OK);
	}
}
