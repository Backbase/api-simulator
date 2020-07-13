package com.backbase.api.simulator.prism;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.boot.actuate.health.Health;

public class PrismHealthIndicatorTest {

    @Test
    public void testHealthy() {
        PrismServer server = mock(PrismServer.class);
        when(server.isRunning()).thenReturn(true);

        PrismHealthIndicator healthIndicator = new PrismHealthIndicator(server);
        assertEquals(Health.up().build(), healthIndicator.health());
    }

    @Test
    public void testUnhealthy() {
        PrismServer server = mock(PrismServer.class);
        when(server.isRunning()).thenReturn(false);

        PrismHealthIndicator healthIndicator = new PrismHealthIndicator(server);
        assertEquals(Health.down().build(), healthIndicator.health());
    }
}
