package com.backbase.api.simulator.response;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.client.ClientHttpResponse;

public class CopyResponseHandler implements ResponseHandler {

    @Override
    public boolean shouldHandle(HttpServletRequest originalRequest) {
        return true;
    }

    @Override
    public void handleContent(HttpServletRequest originalRequest, HttpServletResponse originalResponse,
        ClientHttpResponse clientResponse) throws IOException {
        originalResponse.setStatus(clientResponse.getRawStatusCode());
        clientResponse.getHeaders()
            .forEach((key, value1) -> value1.forEach(value -> originalResponse.addHeader(key, value)));
        ByteStreams.copy(clientResponse.getBody(), originalResponse.getOutputStream());
    }
}
