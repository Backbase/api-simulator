package com.backbase.api.simulator.config;

import com.backbase.api.simulator.validator.DataSizeMin;
import com.backbase.api.simulator.wiremock.WireMockService;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.validation.annotation.Validated;

@Validated
@Configuration
@ConfigurationProperties(prefix = "backbase.api.simulator.performance")
@ConditionalOnProperty(value = ApiSimulatorConfiguration.MODE_CONFIG_KEY, havingValue = "PERFORMANCE")
public class WireMockServerConfiguration {

    private static final String ALL_IP_ADDRESSES = "0.0.0.0";

    @Min(1)
    private int acceptorThreads = 4;

    @Min(1)
    private int acceptQueueSize = 100;

    @NotNull
    @DataSizeMin(value = 8, unit = DataUnit.KILOBYTES)
    private DataSize headerBufferSize = DataSize.ofKilobytes(16);

    @Min(1)
    private int responseThreads = 20;

    @Bean
    WireMockService wireMockService(WireMockConfiguration configuration) {
        return new WireMockService(configuration);
    }

    /**
     * Provides configuration tuned for running WireMock in performance tests.
     *
     * @param configuration Service configuration.
     * @return Performance focused WireMock configuration.
     */
    @Bean
    public WireMockConfiguration wireMockConfiguration(ApiSimulatorConfiguration configuration) {
        return WireMockConfiguration.wireMockConfig()
            .port(configuration.getPort())
            .bindAddress(ALL_IP_ADDRESSES)
            .jettyAcceptors(getAcceptorThreads())
            .jettyAcceptQueueSize(getAcceptQueueSize())
            .jettyHeaderBufferSize(Math.toIntExact(getHeaderBufferSize().toBytes()))
            .asynchronousResponseEnabled(true)
            .asynchronousResponseThreads(getResponseThreads())
            .usingFilesUnderDirectory(configuration.getMappingsDirectory())
            .disableRequestJournal()
            .extensions(new ResponseTemplateTransformer(false));
    }

    public int getAcceptorThreads() {
        return acceptorThreads;
    }

    public void setAcceptorThreads(int acceptorThreads) {
        this.acceptorThreads = acceptorThreads;
    }

    public int getAcceptQueueSize() {
        return acceptQueueSize;
    }

    public void setAcceptQueueSize(int acceptQueueSize) {
        this.acceptQueueSize = acceptQueueSize;
    }

    public DataSize getHeaderBufferSize() {
        return headerBufferSize;
    }

    public void setHeaderBufferSize(DataSize headerBufferSize) {
        this.headerBufferSize = headerBufferSize;
    }

    public int getResponseThreads() {
        return responseThreads;
    }

    public void setResponseThreads(int responseThreads) {
        this.responseThreads = responseThreads;
    }
}
