package com.backbase.api.simulator.prism;

import com.backbase.api.simulator.response.JsonPathResponseHandler;
import com.backbase.api.simulator.response.CopyResponseHandler;
import com.backbase.api.simulator.response.ResponseHandler;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

public class PrismResponseExtractor implements ResponseExtractor<Object> {

    private static final ImmutableList<ResponseHandler> RESPONSE_HANDLERS = ImmutableList.of(
        new JsonPathResponseHandler(),
        new CopyResponseHandler()
    );

    private final HttpServletRequest originalRequest;
    private final HttpServletResponse originalResponse;

    public PrismResponseExtractor(HttpServletRequest originalRequest, HttpServletResponse originalResponse) {
        this.originalRequest = originalRequest;
        this.originalResponse = originalResponse;
    }

    @Override
    public Object extractData(ClientHttpResponse clientResponse) throws IOException {
        RESPONSE_HANDLERS.stream()
            .filter(handler -> handler.shouldHandle(originalRequest))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Couldn't find response handler for " + originalRequest))
            .handleContent(originalRequest, originalResponse, clientResponse);

        return null;
    }
}
