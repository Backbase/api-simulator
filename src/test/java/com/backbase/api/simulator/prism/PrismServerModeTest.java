package com.backbase.api.simulator.prism;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.backbase.api.simulator.config.ApiSimulatorConfiguration;
import com.backbase.api.simulator.config.ApiSimulatorConfigurations;
import com.backbase.api.simulator.util.Paths;
import com.google.common.collect.ImmutableList;
import java.net.MalformedURLException;
import org.junit.jupiter.api.Test;

class PrismServerModeTest {

    @Test
    void testBuildProcessSimulationMode() {
        ApiSimulatorConfiguration configuration = ApiSimulatorConfigurations.defaultConfig();
        int serverPort = 14080;
        ProcessBuilder processBuilder = PrismServerMode.SIMULATION.buildProcess(configuration, serverPort);
        ImmutableList<String> expected = ImmutableList.of(
            Paths.getAbsolutePath(configuration.getPrismPath()), "mock",
            Paths.getSpecPath(configuration.getSpec(), serverPort),
            "-p", Integer.toString(configuration.getPort()),
            "-h", "0.0.0.0");
        assertEquals(expected, processBuilder.command());
    }

    @Test
    void testBuildProcessProxyMode() throws MalformedURLException {
        ApiSimulatorConfiguration configuration = ApiSimulatorConfigurations.proxyMode();
        int serverPort = 14080;
        ProcessBuilder processBuilder = PrismServerMode.PROXY.buildProcess(configuration, serverPort);
        ImmutableList<String> expected = ImmutableList.of(
            Paths.getAbsolutePath(configuration.getPrismPath()), "proxy",
            Paths.getSpecPath(configuration.getSpec(), serverPort),
            configuration.getDownstreamUrl().get().toString(),
            "-p", Integer.toString(configuration.getPort()),
            "-h", "0.0.0.0",
            "--errors");
        assertEquals(expected, processBuilder.command());
    }
}
