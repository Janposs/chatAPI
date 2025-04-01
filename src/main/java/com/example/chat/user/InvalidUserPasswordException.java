package com.example.chat.user;

public class InvalidUserPasswordException extends RuntimeException {
	public InvalidUserPasswordException() {
		super("Invalid user or password");
	}
}
