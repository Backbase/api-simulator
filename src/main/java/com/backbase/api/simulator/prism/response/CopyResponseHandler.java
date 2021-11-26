package com.backbase.api.simulator.prism.response;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.client.ClientHttpResponse;

public class CopyResponseHandler extends AbstractResponseHandler implements ResponseHandler {

    @Override
    public boolean shouldHandle(HttpServletRequest originalRequest, ClientHttpResponse clientResponse) {
        return true;
    }

    @Override
    public void handleContent(HttpServletRequest originalRequest, HttpServletResponse originalResponse,
        ClientHttpResponse clientResponse) throws IOException {
        copyResponseBeginning(originalResponse, clientResponse);
        ByteStreams.copy(clientResponse.getBody(), originalResponse.getOutputStream());
    }
}
