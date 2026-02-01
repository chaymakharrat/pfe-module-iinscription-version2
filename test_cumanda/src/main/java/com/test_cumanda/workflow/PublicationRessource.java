package com.test_cumanda.workflow;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicationRessource {

    private final PublicationService publicationService;

    public PublicationRessource(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @GetMapping("/demarrerProcess")
    public ResponseEntity<String> demarrerProcess() {
        publicationService.demarrerProcess();
        return ResponseEntity.ok("Process démarré avec succès");
    }
    @GetMapping("/video_edite")
    public ResponseEntity<String> notifierVideoEdite() {
        publicationService.notifierMessage();
        return ResponseEntity.ok("notification envoyé");
    }
}
