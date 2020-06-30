package com.backbase.api.simulator.config;

import com.backbase.api.simulator.prism.PrismServerMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

public class ApiSimulatorConfigurations {

    private ApiSimulatorConfigurations() {
    }

    public static ApiSimulatorConfiguration defaultConfig() {
        ApiSimulatorConfiguration config = new ApiSimulatorConfiguration();
        config.setPrismPath(Paths.get("target/prism"));
        config.setBasePath("/client-api/v2");
        config.setSpec("https://api.backbase.cloud/doc/places-management-api-v2.yaml");
        config.setSpecAuthorization(Optional.of("authorization"));
        return config;
    }

    public static ApiSimulatorConfiguration proxyMode() throws MalformedURLException {
        ApiSimulatorConfiguration config = defaultConfig();
        config.setMode(PrismServerMode.PROXY);
        config.setDownstreamUrl(Optional.of(new URL("http://localhost:8080/places-service/client-api/v2")));
        return config;
    }

    public static ApiSimulatorConfiguration withoutSpecAuthorization() {
        ApiSimulatorConfiguration config = defaultConfig();
        config.setSpecAuthorization(Optional.empty());
        return config;
    }

    public static ApiSimulatorConfiguration withSpec(String path) {
        ApiSimulatorConfiguration config = defaultConfig();
        config.setSpec(path);
        return config;
    }
}
