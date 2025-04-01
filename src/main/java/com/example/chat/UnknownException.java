package com.example.chat;

public class UnknownException extends RuntimeException {
	public UnknownException() {
		super("unknown entity");
	}
}
