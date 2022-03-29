package com.devsuperior.dscatalog.controllers.Execeptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devsuperior.dscatalog.services.exceptions.ServiceNotFoundException;

@ControllerAdvice
public class ControllerExceptionHandler {
	
	@ExceptionHandler(ServiceNotFoundException.class)
	public ResponseEntity<StandardErrorResponse> entityNotFound(ServiceNotFoundException e, HttpServletRequest request) {
		StandardErrorResponse error = new StandardErrorResponse();
		HttpStatus errorStatus = HttpStatus.NOT_FOUND;
		
		error.setTimestamp(Instant.now());
		error.setStatus(errorStatus.value());
		error.setError(e.getMessage());
		error.setPath(request.getRequestURI());
		
		return ResponseEntity.status(errorStatus).body(error);
	}

}
