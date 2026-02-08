package com.example.workflow.config;

import com.netflix.discovery.shared.transport.jersey.TransportClientFactories;
import com.netflix.discovery.shared.transport.jersey3.Jersey3TransportClientFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to manually provide the Jersey 3 TransportClientFactories bean.
 * This is required in Spring Boot 3 when Jersey is on the classpath (e.g. from Camunda),
 * as Spring Cloud 4.x may not automatically register this bean.
 */
@Configuration
public class EurekaJersey3Config {

    @Bean
    public TransportClientFactories<?> jersey3TransportClientFactories() {
        return new Jersey3TransportClientFactories();
    }
}
