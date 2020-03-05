package com.backbase.api.simulator.spec;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
                writeResponse(spec.get(), response);
            } else {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                writeResponse("Spec not found", response);
            }
        } catch (RuntimeException e) {
            LOGGER.error("Error providing spec", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            writeResponse("Couldn't provide spec due to unexpected error", response);
        }
    }

    private void writeResponse(String content, HttpServletResponse response) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()))) {
            writer.write(content);
        }
    }
}
