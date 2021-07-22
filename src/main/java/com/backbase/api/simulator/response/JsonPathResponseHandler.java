package com.backbase.api.simulator.response;

import com.backbase.api.simulator.util.HttpResponses;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

public class JsonPathResponseHandler implements ResponseHandler {

    static final String PATH_HEADER_PREFIX = "x-change-path";
    static final String KEY_HEADER_PREFIX = "x-change-key";
    static final String VALUE_HEADER_PREFIX = "x-change-value";

    @Override
    public boolean shouldHandle(HttpServletRequest originalRequest) {
        return Streams.stream(originalRequest.getHeaderNames().asIterator())
            .anyMatch(name -> name.toLowerCase(Locale.ROOT).startsWith(PATH_HEADER_PREFIX));
    }

    @Override
    public void handleContent(HttpServletRequest originalRequest, HttpServletResponse originalResponse,
        ClientHttpResponse clientResponse) throws IOException {
        String resultJson = "";
        try {
            DocumentContext context = replaceValue(originalRequest, clientResponse);
            resultJson = context.jsonString();
        } catch (IllegalArgumentException e) {
            originalResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            String msg = String.format("Couldn't process JSONPath expression, missing or invalid parameters %s: %s",
                ImmutableList.of(PATH_HEADER_PREFIX, KEY_HEADER_PREFIX, VALUE_HEADER_PREFIX), e.getMessage());
            HttpResponses.writeResponse(msg, originalResponse);
            return;
        } catch (InvalidPathException e) {
            originalResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            HttpResponses.writeResponse("Couldn't parse JSONPath expression: " + e.getMessage(), originalResponse);
            return;
        }

        originalResponse.setStatus(clientResponse.getRawStatusCode());
        clientResponse.getHeaders()
            .forEach((key, value1) -> value1.forEach(value -> originalResponse.addHeader(key, value)));

        int responseLength = resultJson.getBytes(originalResponse.getCharacterEncoding()).length;
        originalResponse.setHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(responseLength));

        try (Writer writer = originalResponse.getWriter()) {
            writer.write(resultJson);
        }
    }

    private DocumentContext replaceValue(HttpServletRequest originalRequest, ClientHttpResponse clientResponse)
        throws IOException {
        DocumentContext context = JsonPath.parse(clientResponse.getBody());
        String path = originalRequest.getHeader(PATH_HEADER_PREFIX);
        String key = originalRequest.getHeader(KEY_HEADER_PREFIX);
        String value = originalRequest.getHeader(VALUE_HEADER_PREFIX);

        return context.put(path, key, value);
    }
}
