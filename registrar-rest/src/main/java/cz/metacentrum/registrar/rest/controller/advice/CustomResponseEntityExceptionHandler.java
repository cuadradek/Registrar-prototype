package cz.metacentrum.registrar.rest.controller.advice;

import cz.metacentrum.registrar.service.FormNotFoundException;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler {

	@ExceptionHandler(FormNotFoundException.class)
	public ResponseEntity<ExceptionResponse> formNotFoundHandler(FormNotFoundException ex, HttpServletRequest httpRequest) {
		ExceptionResponse response =  new ExceptionResponse(HttpStatus.NOT_FOUND.value(),
				ex.getMessage(), httpRequest.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponse> accessDeniedExceptionHandler(AccessDeniedException ex, HttpServletRequest httpRequest) {
		ExceptionResponse response =  new ExceptionResponse(HttpStatus.FORBIDDEN.value(),
				ex.getMessage(), httpRequest.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex, HttpServletRequest httpRequest) {
		ExceptionResponse response =  new ExceptionResponse(HttpStatus.FORBIDDEN.value(),
				ex.getMessage(), httpRequest.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> genericExceptionHandler(Exception ex, HttpServletRequest httpRequest) {
		ExceptionResponse response =  new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ex.getMessage(), httpRequest.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Data
	private static class ExceptionResponse {
		private Instant timestamp;
		private int status;
		private String error;
		private String path;

		public ExceptionResponse(int status, String error, String path) {
			this.timestamp = Instant.now();
			this.status = status;
			this.error = error;
			this.path = path;
		}
	}
}
