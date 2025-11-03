package edu.microchat.core.common;

import java.io.IOException;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.server.ResponseStatusException;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
  private final Logger logger = LoggerFactory.getLogger(RestTemplateResponseErrorHandler.class);

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    HttpStatusCode statusCode = response.getStatusCode();

    return statusCode.is5xxServerError();
  }

  @Override
  public void handleError(URI url, HttpMethod method, ClientHttpResponse response)
      throws IOException {
    HttpStatusCode statusCode = response.getStatusCode();

    logger.warn(
        "RestTemplate call failed: {} {} => {} {}", method, url, statusCode.value(), statusCode);
    throw new ResponseStatusException(
        HttpStatus.BAD_GATEWAY, "External request failed. Try again later");
  }
}
