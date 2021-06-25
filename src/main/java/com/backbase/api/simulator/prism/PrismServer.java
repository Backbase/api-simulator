package com.backbase.api.simulator.prism;

import com.backbase.api.simulator.config.ApiSimulatorConfiguration;
import com.backbase.api.simulator.exception.PrismUnavailableException;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Represents and manages a Prism server execution.
 */
public class PrismServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrismServer.class);

    private static final int PROCESS_TIMEOUT = 30;
    private static final TimeUnit PROCESS_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private final ApiSimulatorConfiguration configuration;
    private final Executor executor;
    private final int serverPort;
    private final String applicationName;
    private final RestTemplate restTemplate;

    private final Semaphore processSemaphore = new Semaphore(1);
    private Process process;
    private boolean processSuccessful;

    /**
     * Creates a new instance of the server.
     *
     * @param configuration API Simulator Service configuration.
     * @param executor Executor to use for async tasks.
     * @param serverPort Port the API Simulator is listening on.
     * @param applicationName Application name the API Simulator is running under.
     */
    public PrismServer(ApiSimulatorConfiguration configuration,
        @Qualifier("applicationTaskExecutor") Executor executor,
        @Value("${server.port}") int serverPort,
        @Value("${spring.application.name}") String applicationName) {
        this.configuration = configuration;
        this.executor = executor;
        this.serverPort = serverPort;
        this.applicationName = applicationName;
        this.restTemplate = new RestTemplate();
        this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        this.restTemplate.setErrorHandler(new NoErrorResponseErrorHandler());
    }

    /**
     * Starts the Prism server.
     *
     * @throws IOException If couldn't communicate with the Prism server.
     * @throws InterruptedException If the thread is interrupted during the process.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void start() throws IOException, InterruptedException {
        this.processSemaphore.acquire();
        startPrism();
    }

    private void startPrism() throws IOException {
        ProcessBuilder processBuilder = configuration.getMode().buildProcess(configuration, serverPort);
        LOGGER.info("Executing prism with the following command: {}", processBuilder.command());

        process = processBuilder.start();
        executor.execute(new PrismLogger(this, "Prism", process.getInputStream()));
        executor.execute(new PrismLogger(this, "Prism error", process.getErrorStream()));
        LOGGER.debug("Prism command executed");
    }

    /**
     * Notifies the Prism server has been started.
     *
     * @param success True if the server started successfully.
     */
    public void onPrismStartResult(boolean success) {
        this.processSuccessful = success;
        this.processSemaphore.release();
    }

    /**
     * Forwards an HTTP request to the Prism server and writes the response received.
     *
     * @param request Request to forward.
     * @param response Response to write to.
     * @throws InterruptedException If the execution is interrupted before it's finished.
     * @throws PrismUnavailableException If Prism server is not available.
     */
    public void forward(HttpServletRequest request, HttpServletResponse response)
        throws InterruptedException, PrismUnavailableException {
        try {
            if (this.processSemaphore.tryAcquire(PROCESS_TIMEOUT, PROCESS_TIMEOUT_UNIT)) {
                if (!processSuccessful) {
                    throw new PrismUnavailableException(
                        "Prism server didn't start successfully, cannot forward request");
                }
                doForward(request, response);
            } else {
                throw new PrismUnavailableException("Prism server is not ready yet, cannot forward request");
            }
        } finally {
            this.processSemaphore.release();
        }
    }

    private void doForward(HttpServletRequest request, HttpServletResponse response) {
        String prismPath = request.getPathInfo().replace(getPathPrefix(), "");
        if (!("/".equals(configuration.getBasePath()))
            && prismPath.equals(request.getPathInfo())) { // service-to-service call
            prismPath = request.getPathInfo().replace(configuration.getBasePath(), "");
        }
        String url = String.format("http://localhost:%s/%s", configuration.getPort(), prismPath);
        if (request.getQueryString() != null) {
            url += "?" + request.getQueryString();
        }

        restTemplate.execute(url, HttpMethod.valueOf(request.getMethod()), new PrismRequestCallback(request),
            new PrismResponseExtractor(response));
    }

    /**
     * Stops the Prism server if it's executing.
     *
     * @throws InterruptedException If the execution is interrupted while waiting for Prism to stop.
     */
    @PreDestroy
    public void stop() throws InterruptedException {
        if (process != null) {
            process.destroy();
            if (!process.waitFor(PROCESS_TIMEOUT, PROCESS_TIMEOUT_UNIT)) {
                process.destroyForcibly();
            }
            process = null;
        }
    }

    /**
     * Stops and starts the Prism server.
     *
     * @throws InterruptedException If the execution is interrupted while stopping/starting the Prism server.
     * @throws IOException If couldn't communicate with the Prism server.
     */
    public void restart() throws InterruptedException, IOException {
        if (this.processSemaphore.tryAcquire(PROCESS_TIMEOUT, PROCESS_TIMEOUT_UNIT)) {
            stop();
            startPrism();
        }
    }

    private String getPathPrefix() {
        return "/" + applicationName + configuration.getBasePath();
    }

    public boolean isRunning() {
        return processSuccessful;
    }
}
