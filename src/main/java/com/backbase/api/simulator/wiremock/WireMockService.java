package com.backbase.api.simulator.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

public class WireMockService {

    private static final Logger logger = LoggerFactory.getLogger(WireMockService.class);

    private final WireMockServer server;

    public WireMockService(WireMockConfiguration wireMockConfiguration) {
        this.server = new WireMockServer(wireMockConfiguration);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        logger.info("Starting WireMock server with [{}] mappings", server.getStubMappings().size());
        server.start();
        logger.info("WireMock server has started successfully on port [{}] with [{}] mappings", server.port(),
            server.getStubMappings().size());
    }

    @PreDestroy
    public void stop() {
        logger.info("Stopping WireMock server on port [{}]", server.port());
        server.stop();
    }
}
