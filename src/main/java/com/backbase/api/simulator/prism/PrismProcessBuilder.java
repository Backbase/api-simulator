package com.backbase.api.simulator.prism;

import com.backbase.api.simulator.config.ApiSimulatorConfiguration;
import com.backbase.api.simulator.util.Paths;

public class PrismProcessBuilder {

    private final ApiSimulatorConfiguration configuration;
    private final int serverPort;

    public PrismProcessBuilder(ApiSimulatorConfiguration configuration, int serverPort) {
        this.configuration = configuration;
        this.serverPort = serverPort;
    }

    public ProcessBuilder buildProcess() {
        switch (configuration.getMode()) {
            case SIMULATION:
                return buildSimulationProcess();
            case PROXY:
                return buildProxyProcess();
            default:
                throw new IllegalArgumentException("Mode not supported with Prism: " + configuration.getMode());
        }
    }

    private ProcessBuilder buildSimulationProcess() {
        return new ProcessBuilder(
            Paths.getAbsolutePath(configuration.getPrismPath()), "mock",
            Paths.getSpecPath(configuration.getSpec(), serverPort),
            "-p", Integer.toString(configuration.getPort()),
            "-h", "0.0.0.0");
    }

    private ProcessBuilder buildProxyProcess() {
        return new ProcessBuilder(
            Paths.getAbsolutePath(configuration.getPrismPath()), "proxy",
            Paths.getSpecPath(configuration.getSpec(), serverPort),
            configuration.getDownstreamUrl()
                .orElseThrow(() -> new IllegalArgumentException("Downstream service URL required in proxy mode"))
                .toString(),
            "-p", Integer.toString(configuration.getPort()),
            "-h", "0.0.0.0",
            "--errors");
    }
}
