package com.backbase.api.simulator.prism;

import com.backbase.api.simulator.config.ApiSimulatorConfiguration;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.concurrent.Executor;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class PrismServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrismServer.class);

    private final ApiSimulatorConfiguration configuration;
    private final Executor executor;
    private final String applicationName;

    private Process process;

    public PrismServer(ApiSimulatorConfiguration configuration,
        Executor executor,
        @Value("${spring.application.name}") String applicationName) {
        this.configuration = configuration;
        this.executor = executor;
        this.applicationName = applicationName;
    }

    @PostConstruct
    public void start() throws IOException {
        process = new ProcessBuilder("prism", "mock", "/home/leandro/dev/repo/cards-api/target/openapi.yaml", "-p",
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
        String pathPrefix = getPathPrefix();
        String pathInfo = request.getPathInfo();
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
