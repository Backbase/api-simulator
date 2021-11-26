package com.backbase.api.simulator.prism;

import com.backbase.api.simulator.prism.response.CopyResponseHandler;
import com.backbase.api.simulator.prism.response.JsonPathResponseHandler;
import com.backbase.api.simulator.prism.response.ResponseHandler;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

public class PrismResponseExtractor implements ResponseExtractor<Object> {

    public static final List<ResponseHandler> RESPONSE_HANDLERS = List.of(
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
            .filter(handler -> {
                try {
                    return handler.shouldHandle(originalRequest, clientResponse);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Couldn't find response handler for " + originalRequest))
            .handleContent(originalRequest, originalResponse, clientResponse);

        return null;
    }
}
