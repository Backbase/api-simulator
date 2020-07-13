package com.backbase.api.simulator.prism;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * Checks if Prism is healthy.
 */
public class PrismHealthIndicator implements HealthIndicator {

    private final PrismServer prismServer;

    public PrismHealthIndicator(PrismServer prismServer) {
        this.prismServer = prismServer;
    }

    @Override
    public Health health() {
        return prismServer.isRunning() ? Health.up().build() : Health.down().build();
    }
}
