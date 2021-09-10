package com.backbase.api.simulator.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class NoContentResponseHandlerTest {

    private static final String TEST_LOCATION = "https://backbase.com/test";

    private final NoContentResponseHandler handler = new NoContentResponseHandler();

    @Test
    void testShouldNotHandleSuccessfulResponse() throws IOException {
        MockHttpServletRequest originalRequest = new MockHttpServletRequest();
        MockClientHttpResponse clientResponse = new MockClientHttpResponse(new byte[0], HttpStatus.OK);
        assertFalse(handler.shouldHandle(originalRequest, clientResponse));
    }

    @Test
    void testShouldHandleNotAcceptableResponse() throws IOException {
        MockHttpServletRequest originalRequest = new MockHttpServletRequest();
        originalRequest.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
        MockClientHttpResponse clientResponse = new MockClientHttpResponse(new byte[0], HttpStatus.NOT_ACCEPTABLE);
        assertTrue(handler.shouldHandle(originalRequest, clientResponse));
    }

    @Test
    void testHandleContent() {
        MockHttpServletRequest originalRequest = new MockHttpServletRequest();
        MockHttpServletResponse originalResponse = new MockHttpServletResponse();
        MockClientHttpResponse clientResponse = new MockClientHttpResponse(new byte[0], HttpStatus.NOT_ACCEPTABLE);
        clientResponse.getHeaders().add(HttpHeaders.CONTENT_LENGTH, "30");
        clientResponse.getHeaders().add(HttpHeaders.CONTENT_LOCATION, TEST_LOCATION);

        handler.handleContent(originalRequest, originalResponse, clientResponse);

        assertEquals(HttpStatus.NO_CONTENT.value(), originalResponse.getStatus());
        assertEquals("0", originalResponse.getHeader(HttpHeaders.CONTENT_LENGTH));
        assertEquals(TEST_LOCATION, originalResponse.getHeader(HttpHeaders.CONTENT_LOCATION));
    }
}
