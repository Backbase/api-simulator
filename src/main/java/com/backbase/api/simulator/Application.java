package com.backbase.api.simulator;

import com.backbase.api.simulator.config.ApiSimulatorConfiguration;
import com.backbase.api.simulator.prism.PrismHealthIndicator;
import com.backbase.api.simulator.prism.PrismReloader;
import com.backbase.api.simulator.prism.PrismServer;
import com.backbase.api.simulator.spec.SpecDownloader;
import com.backbase.dbs.ctrl.security.LocalSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@Import({
    ApiSimulatorConfiguration.class,
    LocalSecurityConfig.class,
    PrismHealthIndicator.class,
    PrismReloader.class,
    PrismServer.class,
    ServletConfiguration.class,
    SpecDownloader.class})
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
