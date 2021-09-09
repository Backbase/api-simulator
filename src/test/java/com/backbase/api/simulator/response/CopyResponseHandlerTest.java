package com.backbase.api.simulator.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class CopyResponseHandlerTest {

    private static final String JSON_CONTENT_TYPE = "application/json;charset=ISO-8859-1";
    private static final String BODY = "{ \"test\": 1 }";

    private final CopyResponseHandler handler = new CopyResponseHandler();

    @Test
    void testCopyEverything() throws IOException {
        MockHttpServletRequest originalRequest = new MockHttpServletRequest();
        MockHttpServletResponse originalResponse = new MockHttpServletResponse();
        MockClientHttpResponse clientResponse = new MockClientHttpResponse(BODY.getBytes(StandardCharsets.UTF_8),
            HttpStatus.OK);
        clientResponse.getHeaders().add(HttpHeaders.CONTENT_TYPE, JSON_CONTENT_TYPE);

        assertTrue(handler.shouldHandle(originalRequest, clientResponse));

        handler.handleContent(originalRequest, originalResponse, clientResponse);

        assertEquals(HttpStatus.OK.value(), originalResponse.getStatus());
        assertEquals(JSON_CONTENT_TYPE, originalResponse.getHeader(HttpHeaders.CONTENT_TYPE));
        assertEquals(BODY, originalResponse.getContentAsString());
    }
}
