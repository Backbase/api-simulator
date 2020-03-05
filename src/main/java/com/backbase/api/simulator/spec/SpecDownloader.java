package com.backbase.api.simulator.spec;

import com.backbase.api.simulator.config.ApiSimulatorConfiguration;
import java.net.URI;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SpecDownloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpecDownloader.class);

    private static final String API_BACKBASE_CLOUD = "api.backbase.cloud";

    private final ApiSimulatorConfiguration configuration;
    private final RestTemplate restTemplate;

    public SpecDownloader(ApiSimulatorConfiguration configuration) {
        this.configuration = configuration;
        this.restTemplate = new RestTemplate();
    }

    public Optional<String> download() {
        URI uri = URI.create(configuration.getSpec());

        ResponseEntity<String> response;
        if (configuration.getSpec().contains(API_BACKBASE_CLOUD)) {
            HttpHeaders headers = new HttpHeaders();
            String authorization = configuration.getSpecAuthorization().orElseThrow(
                () -> new IllegalStateException("Authorization configuration must be available to access the API hub"));
            headers.add("Authorization", "Basic " + authorization);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        } else {
            response = restTemplate.getForEntity(uri, String.class);
        }

        if (response.getStatusCode().is2xxSuccessful()) {
            return Optional.ofNullable(response.getBody());
        }
        LOGGER.error("Couldn't download spec from [{}], server response is [{}]", uri, response);

        return Optional.empty();
    }
}
