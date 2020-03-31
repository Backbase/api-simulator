package com.backbase.api.simulator.prism;

import com.backbase.api.simulator.config.ApiSimulatorConfiguration;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.concurrent.Executor;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class PrismServer {

    private final ApiSimulatorConfiguration configuration;
    private final Executor executor;
    private final String serverPort;
    private final String applicationName;
    private final RestTemplate restTemplate;

    private Process process;

    public PrismServer(ApiSimulatorConfiguration configuration,
        @Qualifier("applicationTaskExecutor") Executor executor,
        @Value("${server.port}") String serverPort,
        @Value("${spring.application.name}") String applicationName) {
        this.configuration = configuration;
        this.executor = executor;
        this.serverPort = serverPort;
        this.applicationName = applicationName;
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new NoErrorResponseErrorHandler());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() throws IOException {
        process = new ProcessBuilder(getAbsolutePath(configuration.getPrismPath()),
            "mock", getSpecPath(),
            "-p", Integer.toString(configuration.getPort()),
            "-h", "0.0.0.0")
            .start();

        executor.execute(new PrismLogger("Prism", process.getInputStream()));
        executor.execute(new PrismLogger("Prism error", process.getErrorStream()));
    }

    public void forward(HttpServletRequest request, HttpServletResponse response) {
        String prismPath = request.getPathInfo().replace(getPathPrefix(), "");
        if (prismPath.equals(request.getPathInfo())) { // service-to-service call
            prismPath = request.getPathInfo().replace(configuration.getBasePath(), "");
        }
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

    @PreDestroy
    public void stop() {
        if (process != null) {
            process.destroy();
        }
    }

    private String getPathPrefix() {
        return "/" + applicationName + configuration.getBasePath();
    }

    private String getSpecPath() {
        String spec = configuration.getSpec();
        return spec.startsWith("http") ? "http://localhost:" + serverPort + "/simulator/openapi.yaml"
            : getAbsolutePath(Paths.get(spec));
    }

    private String getAbsolutePath(Path path) {
        return path.normalize().toAbsolutePath().toString();
    }
}
