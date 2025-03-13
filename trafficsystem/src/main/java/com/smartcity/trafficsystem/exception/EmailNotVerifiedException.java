package com.smartcity.trafficsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class EmailNotVerifiedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 745750793365467412L;

	public EmailNotVerifiedException(String message) {
		super(message);
	}
}
