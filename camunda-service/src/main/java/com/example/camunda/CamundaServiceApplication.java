package com.example.camunda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CamundaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CamundaServiceApplication.class, args);
    }
}
