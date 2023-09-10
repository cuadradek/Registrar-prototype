package cz.metacentrum.registrar.rest.controller.advice;

import cz.metacentrum.registrar.service.FormNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler {

	@ExceptionHandler(FormNotFoundException.class)
	Object formNotFoundHandler(FormNotFoundException ex, HttpServletRequest httpRequest) {
		ExceptionResponse response =  new ExceptionResponse(new Date(), HttpStatus.NOT_FOUND.value(),
				ex.getMessage(), httpRequest.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	private static class ExceptionResponse {
		private Date timestamp;
		private int status;
		private String error;
		private String path;

		public ExceptionResponse(Date timestamp, int status, String error, String path) {
			this.timestamp = timestamp;
			this.status = status;
			this.error = error;
			this.path = path;
		}

		public Date getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Date timestamp) {
			this.timestamp = timestamp;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
	}
}
