package com.backbase.api.simulator.prism.response;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.client.ClientHttpResponse;

public interface ResponseHandler {

    boolean shouldHandle(HttpServletRequest originalRequest, ClientHttpResponse clientResponse) throws IOException;

    void handleContent(HttpServletRequest originalRequest, HttpServletResponse originalResponse,
        ClientHttpResponse clientResponse) throws IOException;
}
