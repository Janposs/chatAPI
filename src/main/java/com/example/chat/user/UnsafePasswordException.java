package com.example.chat.user;

public class UnsafePasswordException extends RuntimeException {
	public UnsafePasswordException() {
		super("password does not meet requirements");
	}
			
}
