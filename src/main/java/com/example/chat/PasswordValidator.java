package com.example.chat;

public class PasswordValidator {
	private static final String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{12,}$";

	public PasswordValidator() {
	}

	//is this all I need for this?
	public boolean Validate(String pw) {
		return pw.matches(pattern);
	}
}
