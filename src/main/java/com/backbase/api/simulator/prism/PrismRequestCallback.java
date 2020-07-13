package com.backbase.api.simulator.prism;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.RequestCallback;

public class PrismRequestCallback implements RequestCallback {

    private final HttpServletRequest originalRequest;

    public PrismRequestCallback(HttpServletRequest originalRequest) {
        this.originalRequest = originalRequest;
    }

    @Override
    public void doWithRequest(ClientHttpRequest clientRequest) throws IOException {
        Enumeration<String> headerNames = originalRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            clientRequest.getHeaders().add(headerName, originalRequest.getHeader(headerName));
        }
        ByteStreams.copy(originalRequest.getInputStream(), clientRequest.getBody());
    }
}
