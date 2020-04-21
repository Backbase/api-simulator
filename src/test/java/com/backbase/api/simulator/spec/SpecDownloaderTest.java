package com.backbase.api.simulator.spec;

import static com.backbase.api.simulator.config.ApiSimulatorConfigurations.defaultConfig;
import static com.backbase.api.simulator.config.ApiSimulatorConfigurations.withSpec;
import static com.backbase.api.simulator.config.ApiSimulatorConfigurations.withoutSpecAuthorization;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.backbase.api.simulator.config.ApiSimulatorConfiguration;
import java.net.URI;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class SpecDownloaderTest {

    private static final String API_SPEC_CONTENT = "api-spec-content";

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test(expected = IllegalStateException.class)
    public void testDownloadFromBackbaseCloudWithoutAuthorization() {
        SpecDownloader downloader = new SpecDownloader(withoutSpecAuthorization(), restTemplate);
        downloader.download();
    }

    @Test
    public void testDownloadFromBackbaseCloudWithAuthorization() {
        ApiSimulatorConfiguration config = defaultConfig();

        mockServer.expect(ExpectedCount.once(),
            requestTo(URI.create(config.getSpec())))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "Basic " + config.getSpecAuthorization()
                .orElseThrow(() -> new IllegalStateException("Required spec authorization missing"))))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(API_SPEC_CONTENT)
            );

        SpecDownloader downloader = new SpecDownloader(config, restTemplate);
        Optional<String> spec = downloader.download();
        assertEquals(Optional.of(API_SPEC_CONTENT), spec);
    }

    @Test
    public void testDownloadFromDifferentDomain() {
        ApiSimulatorConfiguration config = withSpec("http://localhost:8080/test.yaml");

        mockServer.expect(ExpectedCount.once(),
            requestTo(URI.create(config.getSpec())))
            .andExpect(method(HttpMethod.GET))
            .andExpect(headerDoesNotExist("Authorization"))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(API_SPEC_CONTENT)
            );

        SpecDownloader downloader = new SpecDownloader(config, restTemplate);
        Optional<String> spec = downloader.download();
        assertEquals(Optional.of(API_SPEC_CONTENT), spec);
    }

    @Test
    public void testUnsuccessfulDownload() {
        ApiSimulatorConfiguration config = defaultConfig();

        mockServer.expect(ExpectedCount.once(),
            requestTo(URI.create(config.getSpec())))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.NOT_FOUND));

        SpecDownloader downloader = new SpecDownloader(config, restTemplate);
        Optional<String> spec = downloader.download();
        assertEquals(Optional.empty(), spec);
    }
}
