package com.example.camunda.config;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Camunda BPM Process Engine.
 * 
 * This class can be used to customize the Camunda process engine configuration.
 * The @EnableProcessApplication annotation enables the Camunda Process Application.
 */
@Configuration
@EnableProcessApplication
public class CamundaConfiguration {

    // You can add custom beans here if needed, for example:
    
    // @Bean
    // public ProcessEnginePlugin customPlugin() {
    //     return new CustomProcessEnginePlugin();
    // }
    
    // @Bean
    // public HistoryEventHandler customHistoryEventHandler() {
    //     return new CustomHistoryEventHandler();
    // }
}
