package pfe.example.enrollement_module.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import pfe.example.enrollement_module.dto.DocumentStatusDTO;
import pfe.example.enrollement_module.dto.EtudiantInfoDTO;

import java.util.List;

@FeignClient(name = "etudiant-service")
public interface EtudiantServiceClient {

    @GetMapping("/api/etudiants/{id}")
    EtudiantInfoDTO getEtudiantById(@PathVariable Long id);

    @GetMapping("/api/documents/etudiant/{etudiantId}/status")
    List<DocumentStatusDTO> getDocumentsStatus(
            @PathVariable("etudiantId") Long etudiantId
    );

    @PutMapping("/api/etudiants/{id}")
    EtudiantInfoDTO updateEtudiant(@PathVariable("id") Long id);


}