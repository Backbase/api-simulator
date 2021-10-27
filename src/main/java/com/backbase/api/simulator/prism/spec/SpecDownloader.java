package com.backbase.api.simulator.prism.spec;

import com.backbase.api.simulator.config.ApiSimulatorConfiguration;
import java.net.URI;
import java.util.Map.Entry;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final ApiSimulatorConfiguration configuration;
    private final RestTemplate restTemplate;

    /**
     * Creates a new instance of the downloader.
     *
     * @param configuration API Simulator Service configuration.
     * @param restTemplate RestTemplate to use to download API specifications.
     */
    public SpecDownloader(ApiSimulatorConfiguration configuration, RestTemplate restTemplate) {
        this.configuration = configuration;
        this.restTemplate = restTemplate;
    }

    /**
     * Downloads the configured API specification.
     *
     * @return API specification content.
     */
    public Optional<String> download() {
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

        return spec;
    }

    private ResponseEntity<String> executeDownload(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        Optional<String> specAuthorization = getSpecAuthorization();
        specAuthorization.ifPresent(s -> headers.add("X-JFrog-Art-Api", s));
        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    private Optional<String> getSpecAuthorization() {
        return configuration.getSpecAuthorizations().entrySet().stream()
            .filter(entry -> configuration.getSpec().contains(entry.getKey()))
            .map(Entry::getValue)
            .findFirst();
    }
}
