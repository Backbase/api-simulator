package com.backbase.api.simulator.spec;

import com.backbase.api.simulator.config.ApiSimulatorConfiguration;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.Executor;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Downloads API specifications to make them available to the server.
 */
public class SpecDownloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpecDownloader.class);

    private static final String API_BACKBASE_CLOUD = "api.backbase.cloud";
    private static final String ARTIFACTS_BACKBASE_COM = "artifacts.backbase.com";

    private final ApiSimulatorConfiguration configuration;
    private final RestTemplate restTemplate;
    private final Executor executor;

    private Optional<String> cachedSpec = Optional.empty();

    /**
     * Creates a new instance of the downloader.
     *
     * @param configuration API Simulator Service configuration.
     * @param restTemplate RestTemplate to use to download API specifications.
     * @param executor Executor to use to preload API spec.
     */
    public SpecDownloader(ApiSimulatorConfiguration configuration,
        RestTemplate restTemplate,
        @Qualifier("applicationTaskExecutor") Executor executor) {
        this.configuration = configuration;
        this.restTemplate = restTemplate;
        this.executor = executor;
    }

    @PostConstruct
    public void preload() {
        LOGGER.info("Preloading API specification [{}] on best effort", configuration.getSpec());
        executor.execute(this::download);
    }

    /**
     * Downloads the configured API specification.
     *
     * @return API specification content.
     */
    public Optional<String> download() {
        if (cachedSpec.isPresent()) {
            return cachedSpec;
        }

        URI uri = URI.create(configuration.getSpec());
        Optional<String> spec = Optional.empty();
        try {
            ResponseEntity<String> response = executeDownload(uri);
            if (response.getStatusCode().is2xxSuccessful()) {
                spec = Optional.ofNullable(response.getBody());
            } else {
                LOGGER.error("Couldn't download spec from [{}], server response is [{}]", uri, response);
            }
        } catch (RestClientException e) {
            LOGGER.error("Couldn't download spec from [{}]", uri, e);
        }
        cachedSpec = spec;

        return spec;
    }

    private ResponseEntity<String> executeDownload(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        if (configuration.getSpec().contains(API_BACKBASE_CLOUD)) {
            String authorization = getRequiredAuthorization();
            headers.add("Authorization", "Basic " + authorization);
        } else if (configuration.getSpec().contains(ARTIFACTS_BACKBASE_COM)) {
            String authorization = getRequiredAuthorization();
            headers.add("X-JFrog-Art-Api", authorization);
        }
        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    private String getRequiredAuthorization() {
        return configuration.getSpecAuthorization().orElseThrow(
            () -> new IllegalStateException("Authorization configuration must be available to download the API spec"));
    }
}
