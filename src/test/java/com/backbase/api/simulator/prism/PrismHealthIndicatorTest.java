package com.backbase.api.simulator.prism;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

class PrismHealthIndicatorTest {

    @Test
    void testHealthy() {
        PrismServer server = mock(PrismServer.class);
        when(server.isRunning()).thenReturn(true);

        PrismHealthIndicator healthIndicator = new PrismHealthIndicator(server);
        assertEquals(Health.up().build(), healthIndicator.health());
    }

    @Test
    void testUnhealthy() {
        PrismServer server = mock(PrismServer.class);
        when(server.isRunning()).thenReturn(false);

        PrismHealthIndicator healthIndicator = new PrismHealthIndicator(server);
        assertEquals(Health.down().build(), healthIndicator.health());
    }
}
