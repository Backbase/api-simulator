package com.backbase.api.simulator.config;

import com.backbase.api.simulator.prism.servlet.ApiSimulatorServlet;
import com.backbase.api.simulator.prism.PrismHealthIndicator;
import com.backbase.api.simulator.prism.PrismProcessBuilder;
import com.backbase.api.simulator.prism.PrismReloader;
import com.backbase.api.simulator.prism.PrismServer;
import com.backbase.api.simulator.prism.servlet.ApiSpecServlet;
import com.backbase.api.simulator.prism.spec.SpecDownloader;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnExpression("'${backbase.api.simulator.mode}'.equals('SIMULATION') || '${backbase.api.simulator.mode}'.equals('PROXY')")
public class PrismServerConfiguration {

    @Bean
    PrismServer prismServer(ApiSimulatorConfiguration configuration,
        PrismProcessBuilder prismProcessBuilder,
        @Qualifier("applicationTaskExecutor") Executor executor,
        @Value("${spring.application.name}") String applicationName) {
        return new PrismServer(configuration, prismProcessBuilder, executor, applicationName);
    }

    @Bean
    PrismProcessBuilder prismProcessBuilder(ApiSimulatorConfiguration configuration,
        @Value("${server.port}") int serverPort) {
        return new PrismProcessBuilder(configuration, serverPort);
    }

    @Bean
    PrismHealthIndicator prismHealthIndicator(PrismServer prismServer) {
        return new PrismHealthIndicator(prismServer);
    }

    @Bean
    PrismReloader prismReloader(PrismServer prismServer) {
        return new PrismReloader(prismServer);
    }

    @Bean
    SpecDownloader specDownloader(ApiSimulatorConfiguration configuration, RestTemplate restTemplate) {
        return new SpecDownloader(configuration, restTemplate);
    }

    @Bean
    ServletRegistrationBean<ApiSimulatorServlet> apiSimulatorServlet(PrismServer prismServer) {
        ApiSimulatorServlet servlet = new ApiSimulatorServlet(prismServer);
        return new ServletRegistrationBean<>(servlet, "/*");
    }

    @Bean
    ServletRegistrationBean<ApiSpecServlet> apiSpecServlet(SpecDownloader specDownloader) {
        ApiSpecServlet servlet = new ApiSpecServlet(specDownloader);
        return new ServletRegistrationBean<>(servlet, "/simulator/openapi.yaml");
    }
}
