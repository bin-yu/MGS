package com.yyy.server.exception;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {
	private Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);
	@ResponseStatus(HttpStatus.CONFLICT) // 409
	@ExceptionHandler(DataIntegrityViolationException.class)
	public void handleConflict(Exception e) {
		logger.warn(e.getMessage(),e);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public void badRequest(Exception e) {
		logger.warn(e.getMessage(),e);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler({ EmptyResultDataAccessException.class, EntityNotFoundException.class })
	public void notfound(Exception e) {
		logger.warn(e.getMessage(),e);
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Throwable.class)
	public void internalError(Throwable e) {
		logger.warn(e.getMessage(),e);
	}
}
