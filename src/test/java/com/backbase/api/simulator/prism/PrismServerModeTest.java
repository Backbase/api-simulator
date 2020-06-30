package com.backbase.api.simulator.prism;

import static org.junit.Assert.assertEquals;

import com.backbase.api.simulator.config.ApiSimulatorConfiguration;
import com.backbase.api.simulator.config.ApiSimulatorConfigurations;
import com.backbase.api.simulator.util.Paths;
import com.google.common.collect.ImmutableList;
import java.net.MalformedURLException;
import org.junit.Test;

public class PrismServerModeTest {

    @Test
    public void testBuildProcessSimulationMode() {
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
    public void testBuildProcessProxyMode() throws MalformedURLException {
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
