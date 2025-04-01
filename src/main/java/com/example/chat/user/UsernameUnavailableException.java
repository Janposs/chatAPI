package com.example.chat.user;

public class UsernameUnavailableException extends RuntimeException {
	public UsernameUnavailableException() {
		super("Username not available");
	}
}
