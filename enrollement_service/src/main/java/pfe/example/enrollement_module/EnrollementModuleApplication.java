package pfe.example.enrollement_module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


import java.time.LocalDateTime;

@SpringBootApplication
@EnableFeignClients
public class EnrollementModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnrollementModuleApplication.class, args);
    }

}

