package com.backbase.api.simulator.prism.response;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.client.ClientHttpResponse;

abstract class AbstractResponseHandler {

    void copyResponseBeginning(HttpServletResponse originalResponse, ClientHttpResponse clientResponse)
        throws IOException {
        copyStatus(originalResponse, clientResponse);
        copyHeaders(originalResponse, clientResponse);
    }

    void copyStatus(HttpServletResponse originalResponse, ClientHttpResponse clientResponse) throws IOException {
        originalResponse.setStatus(clientResponse.getRawStatusCode());
    }

    void copyHeaders(HttpServletResponse originalResponse, ClientHttpResponse clientResponse) {
        clientResponse.getHeaders()
            .forEach((key, value1) -> value1.forEach(value -> originalResponse.addHeader(key, value)));
    }
}
