package pfe.example.etudiantservice;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
//import pfe.example.etudiantservice.service.PaysImportService;
import lombok.extern.slf4j.Slf4j;
import pfe.example.etudiantservice.service.PaysImportService;


@SpringBootApplication
@EnableFeignClients
@Slf4j
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
//@Bean
//CommandLineRunner initPays(PaysImportService service) {
//    return args -> {
//        try {
//            service.importPays();
//            log.error("aaaa");
//        } catch (Exception e) {
//            log.error("Import des pays échoué", e);
//        }
//    };
//}


}
