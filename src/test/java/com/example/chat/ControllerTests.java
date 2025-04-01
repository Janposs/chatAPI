package com.example.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import com.example.chat.chat.Chat;
import com.example.chat.chat.ChatInvite;
import com.example.chat.chat.ChatRecord;
import com.example.chat.chat.ChatService;
import com.example.chat.config.SecurityConfig;
import com.example.chat.message.MessageRecord;
import com.example.chat.message.MessageService;
import com.example.chat.user.LoginRequest;
import com.example.chat.user.RegisterRequest;
import com.example.chat.user.User;
import com.example.chat.user.UserNotFoundException;
import com.example.chat.user.UserRecord;
import com.example.chat.user.UserRepository;
import com.example.chat.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.TestPropertySource;

//TODO: on a lot of the assertions value and expected value are swapped. should be changed.
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(SecurityConfig.class)
class ControllerTest {
	@LocalServerPort
	private int port;
	@Autowired
	private TestRestTemplate template;
	//not sure what that thing was needed for.
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserService userService;
	@Autowired
	private AuthenticationManager authMan;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private ChatService chatService;
	@Autowired
	private MessageService messageService;


	@BeforeAll
	void setup() {
		userService.createUser("test", "X86e.|6M%XZZ", "should contain underscores");
		userService.createUser("test2", "X86e.|6M%XZZ", "lasdjfldsjf");
		userService.createUser("foo3", "G7$pL9x!Vm#Q", "mirFaellt Nix Ein");

		//I can't find this chat through the test user. Maybe the delete test is run before the other one.
		chatService.createChat(1, "a", "b");
		//but this one I can find without any issues
		chatService.createChat(1, "ree", "reeeeee");
		messageService.sendMessage(1, new MessageRecord("I am a message", 1, 1));
		messageService.sendMessage(1, new MessageRecord("another message", 1, 1));
		messageService.sendMessage(1, new MessageRecord("ajvoisaow", 1, 1));
	}

	@Test
	void login() {
		HttpHeaders h2 = new HttpHeaders();
		h2.set("Content-Type", "application/json");
		HttpEntity<LoginRequest> req = new HttpEntity<>(new LoginRequest("test2", "X86e.|6M%XZZ"), h2);
		ResponseEntity<String> resp = template.exchange("/login", HttpMethod.POST, req, String.class);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertNotNull(resp.getBody());
	}

	@Test
	void register() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<RegisterRequest> req = new HttpEntity<RegisterRequest>(
          new RegisterRequest("foo", "X86e.|6M%XZZ", "qwert"), headers);
		HttpEntity<RegisterRequest> usernameConflict = new HttpEntity<RegisterRequest>(
     	  new RegisterRequest("test", "X86e.|6M%XZZ", "wrongName"), headers);
		HttpEntity<RegisterRequest> unsafePassword = new HttpEntity<RegisterRequest>(
    	  new RegisterRequest("foo2", "1234", "qwert"), headers);

		ResponseEntity<?> resp = template.exchange("/register", HttpMethod.POST, req, void.class);
		assertEquals(resp.getStatusCode(), HttpStatus.CREATED);
		//this needs to be string!!! because the exception handler returns the exception message.
		ResponseEntity<?> pw = template.exchange("/register", HttpMethod.POST, unsafePassword, String.class);
		assertEquals(HttpStatus.BAD_REQUEST, pw.getStatusCode());
		ResponseEntity<?> name = template.exchange("/register", HttpMethod.POST, usernameConflict, String.class);
		assertEquals(HttpStatus.CONFLICT, name.getStatusCode());



		//this should probably be separate
		User usr = userService.getByName("foo");
		User u = userService.getByName("test");
		assertNotNull(usr);
		assertTrue(passwordEncoder.matches("X86e.|6M%XZZ", usr.getPassword()));
		//display name should not change if there is a registration request for a username that exist already.
		assertNotEquals(u.getDisplayname(), "WrongName");
		assertThrows(UserNotFoundException.class, () -> {
				userService.getByName("foo2");
		});
	}

	@Test
	void find() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Jwt token = userService.authenticate(new LoginRequest("test2", "X86e.|6M%XZZ"));
		headers.setBearerAuth(token.getTokenValue());
		HttpEntity<Void> req = new HttpEntity<Void>(headers);
		ResponseEntity<?> resp = template.exchange("/user/find/" + "foo", HttpMethod.GET, req, List.class);

		assertEquals(resp.getStatusCode(), HttpStatus.OK);
		assertNotNull(resp.getBody());
		//this thing is bit funky
		String u = resp.getBody().toString();
		u = u.replace("}", "");
		u = u.replace("{", "");
		u = u.replace(",", "");
		var users = Arrays.asList(u.split(" "));
		//users 0 and 2 are the usernames and 1 and 3 are the displaynames
		assertTrue(users.size() == 4, "users size: " + users.size());
		assertTrue(users.get(0).contains("foo3"));
		assertTrue(users.get(1).contains("mirFaellt_Nix_Ein"));
		assertTrue(users.get(2).contains("foo"));
		assertTrue(users.get(3).contains("qwert"));
	}

	@Test
	void chatCreate() {
		HttpHeaders h = new HttpHeaders();
		h.add("Content-Type", "application/json");
		Jwt token = userService.authenticate(new LoginRequest("test2", "X86e.|6M%XZZ"));
		h.setBearerAuth(token.getTokenValue());
		HttpEntity<ChatRecord> req = new HttpEntity<ChatRecord>(new ChatRecord("title", "description"), h);
		ResponseEntity<?> resp = template.exchange("/chat/create", HttpMethod.POST, req, void.class);

		assertEquals(resp.getStatusCode(), HttpStatus.CREATED);
		Chat c = chatService.getChat(3);
		//TODO: add transactional to all services
		assertEquals(c.getTitle(), "title");
		assertEquals(c.getDescription(), "description");
		assertEquals(c.getOwner(), userService.getByName("test2"));
		assertTrue(c.getMembers().contains(userService.getByName("test2")));
	}

	@Test
	void testChatAdd() {
		HttpHeaders h = new HttpHeaders();
		h.add("Content-Type", "application/json");
		Jwt token = userService.authenticate(new LoginRequest("test", "X86e.|6M%XZZ"));
		h.setBearerAuth(token.getTokenValue());
		HttpEntity<ChatInvite> req = new HttpEntity<ChatInvite>(new ChatInvite(1, "test2", 1), h);
		ResponseEntity<?> resp = template.exchange("/chat/add", HttpMethod.POST, req, void.class);

		assertEquals(resp.getStatusCode(), HttpStatus.OK);
		Chat c = chatService.getChat(1);
		assertTrue(c.getMembers().contains(userService.getByName("test2")));
	}

	@Test
	void getChats() {
		HttpHeaders h = new HttpHeaders();
		h.add("Content-Type", "application/json");
		Jwt token = userService.authenticate(new LoginRequest("test", "X86e.|6M%XZZ"));
		h.setBearerAuth(token.getTokenValue());
		HttpEntity<Void> req = new HttpEntity<Void>(h);
		chatService.createChat(1, "neuerChat", "asdlfjsadf");
		ResponseEntity<?> resp = template.exchange("/chat/chats", HttpMethod.GET, req, List.class);

		assertEquals(resp.getStatusCode(), HttpStatus.OK);
		var a = Arrays.asList(resp.getBody().toString().split("}"));
		assertEquals(a.size(), 3);
		//not sure if this is the best way to compare the data
		//TODO: I should probably look into jackson
		assertTrue(a.get(0).contains("chatId=2"));
	}

	@Test
	void deleteChat() {
		HttpHeaders h = new HttpHeaders();
		h.add("Content-Type", "application/json");
		Jwt token = userService.authenticate(new LoginRequest("test", "X86e.|6M%XZZ"));
		h.setBearerAuth(token.getTokenValue());
		HttpEntity<Void> req = new HttpEntity<Void>(h);
		ResponseEntity<?> resp = template.exchange("/chat/delete/1", HttpMethod.DELETE, req, void.class);

		assertEquals(resp.getStatusCode(), HttpStatus.OK);
		assertThrows(UnknownException.class, () -> {
			chatService.getChat(1);
		});
	}

	@Test
	void chatMembers() {
		HttpHeaders h = new HttpHeaders();
		Jwt token = userService.authenticate(new LoginRequest("test", "X86e.|6M%XZZ"));
		h.setBearerAuth(token.getTokenValue());
		HttpEntity<Void> req = new HttpEntity<Void>(h);
		chatService.addUser(1, 2, "test2");
		ResponseEntity<?> resp = template.exchange("/chat/member/2", HttpMethod.GET, req, List.class);

		assertEquals(resp.getStatusCode(), HttpStatus.OK);
		var a = Arrays.asList(resp.getBody().toString().split("}"));
	}

	//TODO: try to remove a user thats not in the chat
	@Test
	void chatLeave() {
		//user 1 owner of chat should not be able to leave
		HttpHeaders h = new HttpHeaders();
		Jwt token = userService.authenticate(new LoginRequest("test", "X86e.|6M%XZZ"));
		h.setBearerAuth(token.getTokenValue());
		HttpEntity<Void> req = new HttpEntity<Void>(h);

		//user 2 not owner of chat should be able to leave
		HttpHeaders h2 = new HttpHeaders();
		Jwt token2 = userService.authenticate(new LoginRequest("test2", "X86e.|6M%XZZ"));
		h2.setBearerAuth(token2.getTokenValue());
		HttpEntity<Void> req2 = new HttpEntity<Void>(h2);

		ResponseEntity<?> resp = template.exchange("/chat/leave/2", HttpMethod.PUT, req, String.class);
		ResponseEntity<?> resp2 = template.exchange("/chat/leave/2", HttpMethod.PUT, req2, String.class);

		assertNotEquals(resp.getStatusCode(), HttpStatus.OK);
		assertEquals(resp2.getStatusCode(), HttpStatus.OK);

		var members = chatService.getMembers(1, 2);
		assertFalse(members.contains(new UserRecord(userService.getByName("test2"))), "test2 should not be in chat 2");		
		assertTrue(members.contains(new UserRecord(userService.getByName("test"))), "test should still be in chat 2");
	}

	@Test
	void chatRemove() {
		chatService.addUser(1, 2, "foo3");
		Chat c = chatService.getChat(2);

		assertTrue(c.getMembers().contains(userService.getByName("foo3")));
		assertTrue(c.getMembers().contains(userService.getByName("test")));
		assertTrue(c.getOwner().getUserId() == userService.getByName("test").getUserId());

		HttpHeaders h = new HttpHeaders();
		Jwt token = userService.authenticate(new LoginRequest("test", "X86e.|6M%XZZ"));
		h.setBearerAuth(token.getTokenValue());
		//only the username is needed.
		HttpEntity<UserRecord> req = new HttpEntity<UserRecord>(new UserRecord("foo3", "mirFaellt_Nix_Ein"), h);
		ResponseEntity<?> resp = template.exchange("/chat/remove/2", HttpMethod.PUT, req, void.class);
		assertEquals(resp.getStatusCode(), HttpStatus.OK);
		assertTrue(c.getMembers().contains(userService.getByName("foo3")));

		HttpEntity<UserRecord> req2 = new HttpEntity<UserRecord>(new UserRecord("test", ""));
		ResponseEntity<?> resp2 = template.exchange("/chat/remove/2", HttpMethod.PUT, req2, void.class);
		assertEquals(HttpStatus.UNAUTHORIZED, resp2.getStatusCode());
		assertTrue(c.getMembers().contains(userService.getByName("test")));
		
	}


	@Test
	void getMessages() {
		HttpHeaders h = new HttpHeaders();
		Jwt token = userService.authenticate(new LoginRequest("test", "X86e.|6M%XZZ"));
		h.setBearerAuth(token.getTokenValue());
		HttpEntity<Void> req = new HttpEntity<Void>(h);
		ResponseEntity<String> resp =
			template.exchange("/chat/messages/1/2", HttpMethod.GET, req, String.class);

		System.out.println(resp.getStatusCode());
		System.out.println(resp.getBody());
	}

	@Test
	void messageSend() {
	}

	@Test
	void messageDelete() {
	}

	@Test
	void messageEdit() {
	}
}
		
		 
