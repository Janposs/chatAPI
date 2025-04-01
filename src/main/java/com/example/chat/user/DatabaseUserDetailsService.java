package com.example.chat.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
	private final UserRepository userRepo;

	public DatabaseUserDetailsService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var u = userRepo.findByUsername(username);
		if (u == null) {
			throw new UsernameNotFoundException("Username not found");
		}

		return u;
	}
}
