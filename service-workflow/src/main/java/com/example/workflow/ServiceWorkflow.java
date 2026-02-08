package com.example.workflow;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients
//@EnableEurekaClient
public class ServiceWorkflow {

  public static void main(String... args) {
    SpringApplication.run(ServiceWorkflow.class, args);
  }

}