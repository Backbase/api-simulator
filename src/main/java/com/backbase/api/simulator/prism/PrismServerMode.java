package com.backbase.api.simulator.prism;

import com.backbase.api.simulator.config.ApiSimulatorConfiguration;
import com.backbase.api.simulator.util.Paths;

public enum PrismServerMode {
    /**
     * Run prism as a server for requests based on an API specification.
     */
    SIMULATION {
        @Override
        public ProcessBuilder buildProcess(ApiSimulatorConfiguration configuration, int serverPort) {
            return new ProcessBuilder(
                Paths.getAbsolutePath(configuration.getPrismPath()), "mock",
                Paths.getSpecPath(configuration.getSpec(), serverPort),
                "-p", Integer.toString(configuration.getPort()),
                "-h", "0.0.0.0");
        }
    },

    /**
     * Run prism as a validating proxy server.
     */
    PROXY {
        @Override
        public ProcessBuilder buildProcess(ApiSimulatorConfiguration configuration, int serverPort) {
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
    },

    PERFORMANCE {
        @Override
        public ProcessBuilder buildProcess(ApiSimulatorConfiguration configuration, int serverPort) {
            return null;
        }
    };

    public abstract ProcessBuilder buildProcess(ApiSimulatorConfiguration configuration, int serverPort);
}
