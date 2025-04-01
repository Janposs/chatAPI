package com.example.chat.user;
//maybe add permissions and stuff later
public record UserRecord(String username, String displayname) {
	public UserRecord(User user) {
		this(user.getUsername(), user.getDisplayname());
	}
}
