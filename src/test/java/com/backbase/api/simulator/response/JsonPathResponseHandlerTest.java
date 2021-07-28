package com.backbase.api.simulator.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class JsonPathResponseHandlerTest {

    private static final String JSON_CONTENT_TYPE = "application/json;charset=ISO-8859-1";
    private static final String BODY = "{ \"test\": \"abc\" }";

    private final JsonPathResponseHandler handler = new JsonPathResponseHandler();

    @Test
    void testShouldNotHandleMissingPath() {
        MockHttpServletRequest originalRequest = new MockHttpServletRequest();
        assertFalse(handler.shouldHandle(originalRequest));
    }

    @Test
    void testHandleMissingJsonPathParameter() throws IOException {
        MockHttpServletRequest originalRequest = new MockHttpServletRequest();
        originalRequest.addHeader(JsonPathResponseHandler.PATH_HEADER_PREFIX, "$");

        MockHttpServletResponse originalResponse = new MockHttpServletResponse();
        MockClientHttpResponse clientResponse = new MockClientHttpResponse(BODY.getBytes(StandardCharsets.UTF_8),
            HttpStatus.OK);
        clientResponse.getHeaders().add(HttpHeaders.CONTENT_TYPE, JSON_CONTENT_TYPE);

        assertTrue(handler.shouldHandle(originalRequest));

        handler.handleContent(originalRequest, originalResponse, clientResponse);

        assertEquals(HttpStatus.BAD_REQUEST.value(), originalResponse.getStatus());
        assertEquals(
            "Couldn't process JSONPath expression, missing or invalid parameters [x-change-path, x-change-key, x-change-value]: key can not be null or empty",
            originalResponse.getContentAsString());
    }

    @Test
    void testHandleInvalidJsonPath() throws IOException {
        MockHttpServletRequest originalRequest = new MockHttpServletRequest();
        originalRequest.addHeader(JsonPathResponseHandler.PATH_HEADER_PREFIX, "$.test[]");
        originalRequest.addHeader(JsonPathResponseHandler.KEY_HEADER_PREFIX, "test");
        originalRequest.addHeader(JsonPathResponseHandler.VALUE_HEADER_PREFIX, "new_value");

        MockHttpServletResponse originalResponse = new MockHttpServletResponse();
        MockClientHttpResponse clientResponse = new MockClientHttpResponse(BODY.getBytes(StandardCharsets.UTF_8),
            HttpStatus.OK);
        clientResponse.getHeaders().add(HttpHeaders.CONTENT_TYPE, JSON_CONTENT_TYPE);

        assertTrue(handler.shouldHandle(originalRequest));

        handler.handleContent(originalRequest, originalResponse, clientResponse);

        assertEquals(HttpStatus.BAD_REQUEST.value(), originalResponse.getStatus());
        assertEquals(
            "Couldn't parse JSONPath expression: Could not parse token starting at position 6. Expected ?, ', 0-9, * ",
            originalResponse.getContentAsString());
    }

    @Test
    void testHandleValidJsonPath() throws IOException {
        MockHttpServletRequest originalRequest = new MockHttpServletRequest();
        originalRequest.addHeader(JsonPathResponseHandler.PATH_HEADER_PREFIX, "$");
        originalRequest.addHeader(JsonPathResponseHandler.KEY_HEADER_PREFIX, "test");
        originalRequest.addHeader(JsonPathResponseHandler.VALUE_HEADER_PREFIX, "new_value");

        MockHttpServletResponse originalResponse = new MockHttpServletResponse();
        MockClientHttpResponse clientResponse = new MockClientHttpResponse(BODY.getBytes(StandardCharsets.UTF_8),
            HttpStatus.OK);
        clientResponse.getHeaders().add(HttpHeaders.CONTENT_TYPE, JSON_CONTENT_TYPE);

        assertTrue(handler.shouldHandle(originalRequest));

        handler.handleContent(originalRequest, originalResponse, clientResponse);

        assertEquals(HttpStatus.OK.value(), originalResponse.getStatus());
        assertEquals("{\"test\":\"new_value\"}", originalResponse.getContentAsString());
    }
}
