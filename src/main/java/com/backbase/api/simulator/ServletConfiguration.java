package com.backbase.api.simulator;

import com.backbase.api.simulator.prism.PrismServer;
import com.backbase.api.simulator.spec.ApiSpecServlet;
import com.backbase.api.simulator.spec.SpecDownloader;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfiguration {

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
