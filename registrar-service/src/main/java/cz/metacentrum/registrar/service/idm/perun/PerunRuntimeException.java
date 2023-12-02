package cz.metacentrum.registrar.service.idm.perun;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

public class PerunRuntimeException extends RuntimeException {
	private String errorId;
	private String name;

	private PerunRuntimeException(String message, Throwable cause, String name, String errorId) {
		super(message, cause);
		this.name = name;
		this.errorId = errorId;
	}

	public static PerunRuntimeException to(HttpClientErrorException ex) {
		try {
			cz.metacentrum.perun.openapi.model.PerunException pe = (cz.metacentrum.perun.openapi.model.PerunException)(new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(ex.getResponseBodyAsByteArray(), cz.metacentrum.perun.openapi.model.PerunException.class);
			return new PerunRuntimeException(pe.getName() + ": " + pe.getMessage(), ex, pe.getName(), pe.getErrorId());
		} catch (IOException var2) {
			return new PerunRuntimeException("cannot parse remote Exception", ex, "", "");
		}
	}

	public String getErrorId() {
		return this.errorId;
	}

	public String getName() {
		return this.name;
	}
}