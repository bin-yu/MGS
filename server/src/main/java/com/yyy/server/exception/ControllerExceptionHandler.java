package com.yyy.server.exception;

import javax.persistence.EntityNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {
	@ResponseStatus(HttpStatus.CONFLICT) // 409
	@ExceptionHandler(DataIntegrityViolationException.class)
	public void handleConflict() {
		// Nothing to do
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public void badRequest() {
		// Nothing to do
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler({ EmptyResultDataAccessException.class, EntityNotFoundException.class })
	public void notfound() {
		// Nothing to do
	}
}
