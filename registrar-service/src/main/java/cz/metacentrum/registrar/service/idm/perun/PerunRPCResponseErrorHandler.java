package cz.metacentrum.registrar.service.idm.perun;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

public class PerunRPCResponseErrorHandler extends DefaultResponseErrorHandler {

	@Override
	public void handleError(ClientHttpResponse response, HttpStatusCode statusCode) throws IOException {
		try {
			super.handleError(response, statusCode);
		} catch (HttpClientErrorException ex) {
			throw PerunRuntimeException.to(ex);
		}
	}
}
