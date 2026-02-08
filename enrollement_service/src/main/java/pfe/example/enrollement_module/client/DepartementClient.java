//package pfe.example.enrollement_module.client;
//
//import jakarta.validation.constraints.Min;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import pfe.example.enrollement_module.model.DiplomeAEtudier;
//import pfe.example.enrollement_module.model.Niveau_diplome_specifique;
//
//import java.util.List;
//
//@FeignClient(name = "departement-service")
//public interface DepartementClient {
//    @GetMapping("/api/diplomes/nom/{nom}")
//    DiplomeAEtudier getDiplomeByNom(@PathVariable("nom") String nom);
//
//    @GetMapping("/api/diplomes/{diplomeId}/can-accept-enrollment")
//    Boolean canAcceptEnrollment(
//            @PathVariable("diplomeId") Long diplomeId,
//            @RequestParam("currentEnrollmentCount") int currentEnrollmentCount
//    );
//
//    @GetMapping("/api/diplomes/nom/{nomDiplome}/frais")
//    Double getFraisInscription(@PathVariable("nomDiplome") String nomDiplome);
//
//    @GetMapping("/{departementId}")
//    List<DiplomeAEtudier> getDiplomesByDepartement(@PathVariable @Min(1) Long departementId);
//    @GetMapping
//    List<DiplomeAEtudier> getAllDiplomes();
//    @GetMapping("/{id}/niveaux")
//    List<Niveau_diplome_specifique> getNiveauxParDiplome(@PathVariable Long id);
//}
