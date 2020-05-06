package com.backbase.api.simulator;

import com.backbase.api.simulator.exception.PrismUnavailableException;
import com.backbase.api.simulator.prism.PrismServer;
import com.backbase.api.simulator.util.HttpResponses;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

public class ApiSimulatorServlet extends HttpServlet {

    private final PrismServer prismServer;

    public ApiSimulatorServlet(PrismServer prismServer) {
        this.prismServer = prismServer;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            prismServer.forward(request, response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            HttpResponses.writeResponse("Couldn't forward request, server is stopping", response);
        } catch (PrismUnavailableException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            HttpResponses.writeResponse(e.getMessage(), response);
        }
    }
}
