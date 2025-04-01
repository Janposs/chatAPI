package com.example.chat;

import com.example.chat.user.InvalidUserPasswordException;
import com.example.chat.user.UnsafePasswordException;
import com.example.chat.user.UsernameUnavailableException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	//I don't really know why these need to be here. It works fine without them.
	//but it fails in testing for some reason.
	//apperently spring does not automatically register exception handlers in testing.
	@ExceptionHandler(InvalidUserPasswordException.class)
	public String handleInvalidUserPasswordException(InvalidUserPasswordException e) {
		return e.getMessage();
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(UnsafePasswordException.class)
	public String handleUnsafePasswordException(UnsafePasswordException e) {
		return e.getMessage();
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(UsernameUnavailableException.class)
	public String handleUsernameUnavailableException(UsernameUnavailableException e) {
		return e.getMessage();
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(PermissionException.class)
	public String handlePermissionException(PermissionException e) {
		return e.getMessage();
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(UnknownException.class)
	public String handleUnknownException(UnknownException e) {
		return e.getMessage();
	}
}
