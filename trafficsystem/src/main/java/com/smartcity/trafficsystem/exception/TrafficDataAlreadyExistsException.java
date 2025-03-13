package com.smartcity.trafficsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TrafficDataAlreadyExistsException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1064153462990535327L;

	
	public TrafficDataAlreadyExistsException(String message) {
		super(message);
	}
}
