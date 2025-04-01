package com.example.chat.user;

import java.util.ArrayList;

import com.example.chat.PasswordValidator;
import com.example.chat.TokenService;
import com.example.chat.chat.ChatRecord;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	private final UserRepository userRepo;
	private PasswordValidator passwordValidator;
	private PasswordEncoder passwordEncoder;
	private AuthenticationManager authMan;
	private TokenService tokenService;
	
	public UserService(UserRepository userRepo, PasswordValidator passwordValidator, PasswordEncoder passwordEncoder, AuthenticationManager authMan, TokenService tokenService) {
		this.authMan = authMan;
		this.userRepo = userRepo;
		this.passwordValidator = passwordValidator;
		this.passwordEncoder = passwordEncoder;
		this.tokenService = tokenService;
	}

	@Transactional
	public Jwt authenticate(LoginRequest req) {
		User u = userRepo.findByUsername(req.username());
		if (u == null) {
			throw new InvalidUserPasswordException();
		}

		
		Authentication auth = authMan.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
		
		return tokenService.generateToken(auth, u.getUserId());
	}

	//should the user be loged out and ask to log in again?
	@Transactional
	public void changePassword(int userId, String password) {
		User user = getUser(userId);
		if (!passwordValidator.Validate(password)) {
			throw new UnsafePasswordException();
		}
		
		user.setPassword(passwordEncoder.encode(password));
		userRepo.save(user);
	}

	@Transactional
	public void changeUsername(int userId, String newUsername) {
		User user = getUser(userId);
		//replace spaces with _. I could also throw an exception if the name
		//contains spaces.
		String nn = newUsername.replace(" ", "_");
		User check = userRepo.findByUsername(nn);
		if (check != null) {
			throw new InvalidUserPasswordException();
		}
		
		user.setUsername(nn);
		userRepo.save(user);
	}

	@Transactional
	public void changeDisplayname(int userId, String newDisplayname) {
		User user = getUser(userId);
		user.setDisplayname(newDisplayname.replace(" ", "_"));
		userRepo.save(user);
	}

	@Transactional
	public void createUser(String username, String password, String displayname) {
		if (!passwordValidator.Validate(password)) {
			throw new UnsafePasswordException();
		}

		username = username.replace(" ", "_");
		displayname = displayname.replace(" ", "_");
		User check = userRepo.findByUsername(username);
		if (check != null) {
			throw new UsernameUnavailableException();
		}
				
		User u = new User();
		u.setUsername(username);
		u.setPassword(passwordEncoder.encode(password));
		u.setDisplayname(displayname);
		userRepo.save(u);
	}

	@Transactional
	public void deleteUser() {
	}

	@Transactional
	public UserRecord getUserRecord(int userId) {
		var user = userRepo.findById(userId);
		if (user.isEmpty()) {
			throw new UserNotFoundException();
		}

		return new UserRecord(user.get().getUsername(), user.get().getDisplayname());
	}

	@Transactional
	public User getUser(int userId) {
		var user = userRepo.findById(userId);
		if (user.isEmpty()) {
			throw new UserNotFoundException();
		}

		return user.get();
	}

	@Transactional
	public User getByName(String username) {
		var user = userRepo.findByUsername(username);
		if (user == null) {
			throw new UserNotFoundException();
		}

		return user;
	}

	@Transactional
	public ArrayList<ChatRecord> getChats(int userId) {
		User user = this.getUser(userId);
		ArrayList<ChatRecord> chats = new ArrayList<>();
		user.getChats().forEach(c -> chats.add(new ChatRecord(c)));
		return chats;
	}

	@Transactional
	public ArrayList<ChatRecord> getOwnedChats(int userId) {
		User user = this.getUser(userId);
		ArrayList<ChatRecord> chats = new ArrayList<>();
		user.getOwnedChats().forEach(c -> chats.add(new ChatRecord(c)));
		return chats;
	}

	@Transactional
	public ArrayList<UserRecord> find(String username) {
		var users = userRepo.findByUsernameStartingWith(username);
		ArrayList<UserRecord> u = new ArrayList<>();
		users.forEach(usr -> u.add(new UserRecord(usr)));
		return u;
	}
}
