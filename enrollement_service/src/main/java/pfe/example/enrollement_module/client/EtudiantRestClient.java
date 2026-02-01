//package pfe.example.enrollement_module.client;
//
//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import pfe.example.enrollement_module.model.Etudiant;
//
//import java.util.List;
//
//@FeignClient(name = "etudiant-service") // Nom du microservice étudiant
//public interface EtudiantRestClient {
//
//    @GetMapping("/etudiants/{id}")
////    @CircuitBreaker(name="etudiant-service",fallbackMethod a= "getDefaultEtudiant")
//    Etudiant getEtudiantById(@PathVariable("id") Long id);
//    //un fallbakc qui correspond a la signature de la methode feign
//    //l’exception est le signal qui dit au circuit breaker : “Utilise ce fallback, quelque chose a échoué”
//    default Etudiant getDefaultEtudiant(Long id,Exception exception) {
//
//    Etudiant etudiant = new Etudiant();
//    etudiant.setId(id);
//    etudiant.setMatricule("matricule");
//    return etudiant;}
//    @GetMapping("/etudiants")
////    @CircuitBreaker(name="etudiant-service",fallbackMethod = "getAllEtudiants")
//    List<Etudiant> getAllEtudiant();
//    default List<Etudiant> getAllEtudiants(Exception exception) {
//        return List.of(new Etudiant());
//    }
//}
