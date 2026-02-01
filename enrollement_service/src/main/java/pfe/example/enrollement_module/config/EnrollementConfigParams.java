package pfe.example.enrollement_module.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="enrollement.params")
public record EnrollementConfigParams(int a,int b) {

}
