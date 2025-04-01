package com.example.chat.user;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException() {
		super("unable to find user");
	}
}
