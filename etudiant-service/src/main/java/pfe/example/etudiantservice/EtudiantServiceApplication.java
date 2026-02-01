package pfe.example.etudiantservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class EtudiantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtudiantServiceApplication.class, args);
    }
//    @Bean
//    public CommandLineRunner init(EtudiantRepository etudiantRepository) {
//        return args -> {
//            etudiantRepository.save(Etudiant.builder().nom("Kharrat")
//                    .prenom("Chaima").email("chaima.khrrat6@gmail.com")
//                    .build());
//            etudiantRepository.save(Etudiant.builder().nom("Kharrat")
//                    .prenom("Melek").email("melek.khrrat@gmail.com")
//                    .build());
//        };
//    }

}
