package com.backbase.api.simulator.config;

import java.nio.file.Path;
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

    @Min(1)
    private int port = 4001;

    @NotBlank
    @Pattern(regexp = "/.*")
    private String basePath;

    @NotNull
    private Path prismPath;

    @NotBlank
    private String spec;

    private Optional<String> specAuthorization;

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

    public Optional<String> getSpecAuthorization() {
        return specAuthorization;
    }

    public void setSpecAuthorization(Optional<String> specAuthorization) {
        this.specAuthorization = specAuthorization;
    }
}
