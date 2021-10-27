package com.backbase.api.simulator.config;

import com.backbase.api.simulator.wiremock.WireMockService;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = ApiSimulatorConfiguration.MODE_CONFIG_KEY, havingValue = "PERFORMANCE")
public class WireMockServerConfiguration {

    private static final String ALL_IP_ADDRESSES = "0.0.0.0";

    @Bean
    WireMockService wireMockService(WireMockConfiguration configuration) {
        return new WireMockService(configuration);
    }

    /**
     * Provides configuration tuned for running WireMock in performance tests.
     *
     * @return Performance focused WireMock configuration.
     */
    @Bean
    public WireMockConfiguration wireMockConfiguration(ApiSimulatorConfiguration configuration) {
        return WireMockConfiguration.wireMockConfig()
            .port(configuration.getPort())
            .bindAddress(ALL_IP_ADDRESSES)
            .jettyAcceptors(4)
            .jettyAcceptQueueSize(100)
            .jettyHeaderBufferSize(16 * 1024)
            .asynchronousResponseEnabled(true)
            .asynchronousResponseThreads(20)
            .usingFilesUnderClasspath(".")
            .disableRequestJournal();
    }
}
