package com.backbase.api.simulator.config;

public enum ServerMode {
    /**
     * Simulate behavior given an API specification.
     */
    SIMULATION,

    /**
     * Validate requests and responses as a proxy server given an API specification.
     */
    PROXY,

    /**
     * Simulate behavior given request/response mappings for performance tests.
     */
    PERFORMANCE;
}
