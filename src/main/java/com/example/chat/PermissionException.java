package com.example.chat;

public class PermissionException extends RuntimeException {
	public PermissionException() {
		super("no permission");
	}
}
