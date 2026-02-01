package pfe.example.enrollement_module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pfe.example.enrollement_module.config.EnrollementConfigParams;


import java.time.LocalDateTime;

@SpringBootApplication
//@EnableFeignClients
@EnableConfigurationProperties(EnrollementConfigParams.class)
public class EnrollementModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnrollementModuleApplication.class, args);
    }

}

