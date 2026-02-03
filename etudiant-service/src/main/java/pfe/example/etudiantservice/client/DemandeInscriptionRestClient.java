//package pfe.example.etudiantservice.client;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.*;
//import pfe.example.etudiantservice.model.DemandeInscription;
//
//@FeignClient(name = "enrollement-service")
//public interface DemandeInscriptionRestClient {
//
//    @GetMapping("/api/demandes/{id}")
//    DemandeInscription getDemandeById(@PathVariable("id") Long id);
//
//    @PutMapping("/api/demandes/{id}/accepter")
//    void accepterDemande(@PathVariable("id") Long id);
//}
