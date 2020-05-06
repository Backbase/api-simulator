package com.backbase.api.simulator.spec;

import com.backbase.api.simulator.util.HttpResponses;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class ApiSpecServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiSpecServlet.class);

    private final SpecDownloader specDownloader;

    public ApiSpecServlet(SpecDownloader specDownloader) {
        this.specDownloader = specDownloader;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Optional<String> spec = specDownloader.download();
            if (spec.isPresent()) {
                response.setStatus(HttpStatus.OK.value());
                HttpResponses.writeResponse(spec.get(), response);
            } else {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                HttpResponses.writeResponse("Spec not found", response);
            }
        } catch (RuntimeException e) {
            LOGGER.error("Error providing spec", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            HttpResponses.writeResponse("Couldn't provide spec due to unexpected error", response);
        }
    }
}
