package com.backbase.api.simulator.response;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Handles the case when the client asks for specific content types but the API spec only defines a 204 No Content
 * response. Prism has a bug that makes it return a 406 Not Acceptable response instead of 204.
 *
 * <p>Can be dropped once https://github.com/stoplightio/prism/issues/1618 is fixed.
 */
public class NoContentResponseHandler extends AbstractResponseHandler implements ResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoContentResponseHandler.class);

    @Override
    public boolean shouldHandle(HttpServletRequest originalRequest, ClientHttpResponse clientResponse)
        throws IOException {
        return originalRequest.getHeader("Accept") != null
            && clientResponse.getStatusCode() == HttpStatus.NOT_ACCEPTABLE;
    }

    @Override
    public void handleContent(HttpServletRequest originalRequest, HttpServletResponse originalResponse,
        ClientHttpResponse clientResponse) {
        LOGGER.warn(
            "Prism returned 406 response, converting it to 204 because it's probably caused by https://github.com/stoplightio/prism/issues/1618");
        originalResponse.setStatus(HttpStatus.NO_CONTENT.value());
        clientResponse.getHeaders().entrySet().stream()
            .filter(entry -> !HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(entry.getKey()))
            .filter(entry -> !HttpHeaders.CONTENT_LENGTH.equalsIgnoreCase(entry.getKey()))
            .forEach(entry -> entry.getValue().forEach(value -> originalResponse.addHeader(entry.getKey(), value)));
        originalResponse.addHeader(HttpHeaders.CONTENT_LENGTH, "0");
    }
}
