package com.backbase.api.simulator;

import com.backbase.api.simulator.prism.PrismServer;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApiSimulatorServlet extends HttpServlet {

    private final PrismServer prismServer;

    public ApiSimulatorServlet(PrismServer prismServer) {
        this.prismServer = prismServer;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        prismServer.forward(request, response);
    }
}
