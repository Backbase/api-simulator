package com.backbase.api.simulator;

import com.backbase.api.simulator.config.ApiSimulatorConfiguration;
import com.backbase.api.simulator.prism.PrismServer;
import com.backbase.dbs.ctrl.security.LocalSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ServletComponentScan
@EnableAsync
@Import({ApiSimulatorConfiguration.class, LocalSecurityConfig.class, PrismServer.class})
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
