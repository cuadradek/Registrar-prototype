package cz.metacentrum.registrar.rest.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

@Data
public class ExceptionResponse {
	@Schema(description = "timestamp of error", example = "2023-10-15T15:40:23.947911375Z")
	private Instant timestamp;
	@Schema(description = "HTTP status code", example = "404")
	private int status;
	@Schema(description = "error message", example = "From not found")
	private String error;
	@Schema(description = "URL path", example = "/forms/1")
	private String path;

	public ExceptionResponse(int status, String error, String path) {
		this.timestamp = Instant.now();
		this.status = status;
		this.error = error;
		this.path = path;
	}
}
