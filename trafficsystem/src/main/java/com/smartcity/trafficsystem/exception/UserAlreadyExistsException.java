package com.smartcity.trafficsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsException extends RuntimeException{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -628556804149484448L;

	public UserAlreadyExistsException(String message) {
		super(message);
	}
}
