package com.backbase.api.simulator.prism;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

public class PrismResponseExtractor implements ResponseExtractor<Object> {

    private final HttpServletResponse originalResponse;

    public PrismResponseExtractor(HttpServletResponse originalResponse) {
        this.originalResponse = originalResponse;
    }

    @Override
    public Object extractData(ClientHttpResponse clientResponse) throws IOException {
        originalResponse.setStatus(clientResponse.getRawStatusCode());
        clientResponse.getHeaders()
            .forEach((key, value1) -> value1.forEach(value -> originalResponse.addHeader(key, value)));
        ByteStreams.copy(clientResponse.getBody(), originalResponse.getOutputStream());
        return null;
    }
}
