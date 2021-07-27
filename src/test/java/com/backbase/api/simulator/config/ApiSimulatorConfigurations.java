package com.backbase.api.simulator.config;

import com.backbase.api.simulator.prism.PrismServerMode;
import com.google.common.collect.ImmutableMap;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

public class ApiSimulatorConfigurations {

    private ApiSimulatorConfigurations() {}

    public static ApiSimulatorConfiguration defaultConfig() {
        ApiSimulatorConfiguration config = new ApiSimulatorConfiguration();
        config.setPrismPath(Paths.get("target/prism"));
        config.setBasePath("/");
        config.setSpec(
            "https://repo.backbase.com/native/specs/transaction-manager/transaction-manager-client-api-v2.5.0.yaml");
        config.setSpecAuthorizations(ImmutableMap.of("repo.backbase.com", "authorization"));
        return config;
    }

    public static ApiSimulatorConfiguration proxyMode() throws MalformedURLException {
        ApiSimulatorConfiguration config = defaultConfig();
        config.setMode(PrismServerMode.PROXY);
        config.setDownstreamUrl(Optional.of(new URL("http://localhost:8080/places-service/client-api/v2")));
        return config;
    }

    public static ApiSimulatorConfiguration withSpec(String path) {
        ApiSimulatorConfiguration config = defaultConfig();
        config.setSpec(path);
        return config;
    }
}
