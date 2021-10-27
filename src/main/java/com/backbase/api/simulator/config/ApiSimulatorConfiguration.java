package com.backbase.api.simulator.config;

import com.google.common.collect.ImmutableMap;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "backbase.api.simulator")
@Validated
public class ApiSimulatorConfiguration {

    public static final String MODE_CONFIG_KEY = "backbase.api.simulator.mode";

    /**
     * Port number prism will be listening on.
     */
    @Min(1)
    private int port = 4001;

    /**
     * Base path of URLs that will be served.
     */
    @NotBlank
    @Pattern(regexp = "/.*")
    private String basePath;

    /**
     * Path to prism's executable.
     */
    @NotNull
    private Path prismPath;

    /**
     * File path or URL of API specification to be used.
     */
    @NotBlank
    private String spec;

    /**
     * Authorization configuration to obtain API specification if it's a URL.
     */
    private Map<String, String> specAuthorizations = ImmutableMap.of();

    /**
     * Execution mode of prism.
     */
    @NotNull
    private ServerMode mode;

    /**
     * URL of downstream service if mode is PROXY.
     */
    private Optional<URL> downstreamUrl = Optional.empty();

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Path getPrismPath() {
        return prismPath;
    }

    public void setPrismPath(Path prismPath) {
        this.prismPath = prismPath;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public Map<String, String> getSpecAuthorizations() {
        return specAuthorizations;
    }

    public void setSpecAuthorizations(Map<String, String> specAuthorizations) {
        this.specAuthorizations = specAuthorizations;
    }

    public ServerMode getMode() {
        return mode;
    }

    public void setMode(ServerMode mode) {
        this.mode = mode;
    }

    public Optional<URL> getDownstreamUrl() {
        return downstreamUrl;
    }

    public void setDownstreamUrl(Optional<URL> downstreamUrl) {
        this.downstreamUrl = downstreamUrl;
    }
}
