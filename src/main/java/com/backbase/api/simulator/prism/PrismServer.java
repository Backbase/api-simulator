package com.backbase.api.simulator.prism;

import com.backbase.api.simulator.config.ApiSimulatorConfiguration;
import com.google.common.io.ByteStreams;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.Enumeration;
import java.util.concurrent.Executor;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class PrismServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrismServer.class);

    private final ApiSimulatorConfiguration configuration;
    private final Executor executor;
    private final String applicationName;
    private final RestTemplate restTemplate;

    private Process process;

    public PrismServer(ApiSimulatorConfiguration configuration,
        Executor executor,
        @Value("${spring.application.name}") String applicationName) {
        this.configuration = configuration;
        this.executor = executor;
        this.applicationName = applicationName;
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
                throw new IllegalStateException("Not supposed to be called");
            }
        });
    }

    @PostConstruct
    public void start() throws IOException {
        process = new ProcessBuilder(configuration.getPrismPath(), "mock",
            "/home/leandro/dev/repo/cards-api/target/openapi.yaml", "-p",
            Integer.toString(configuration.getPort()))
            .start();
        executor.execute(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    LOGGER.info("Prism: {}", line);
                }
            } catch (InterruptedIOException e) {
                // Nothing to do
            } catch (IOException e) {
                LOGGER.error("Couldn't read output from prism server", e);
            }
        });
    }

    public void forward(HttpServletRequest request, HttpServletResponse response) {
        String prismPath = request.getPathInfo().replace(getPathPrefix(), "");
        String url = String.format("http://localhost:%s/%s", configuration.getPort(), prismPath);

        if (request.getQueryString() != null) {
            url += "?" + request.getQueryString();
        }

        restTemplate.execute(url, HttpMethod.valueOf(request.getMethod()), clientRequest -> {
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                clientRequest.getHeaders().add(headerName, request.getHeader(headerName));
            }
            ByteStreams.copy(request.getInputStream(), clientRequest.getBody());
        }, clientResponse -> {
            response.setStatus(clientResponse.getRawStatusCode());
            clientResponse.getHeaders()
                .forEach((key, value1) -> value1.forEach(value -> response.addHeader(key, value)));
            ByteStreams.copy(clientResponse.getBody(), response.getOutputStream());
            return null;
        });
    }

    private String getPathPrefix() {
        return "/" + applicationName + configuration.getBasePath();
    }

    @PreDestroy
    public void stop() {
        if (process != null) {
            process.destroy();
        }
    }
}
