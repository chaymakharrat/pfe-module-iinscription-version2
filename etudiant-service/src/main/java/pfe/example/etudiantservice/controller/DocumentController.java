package pfe.example.etudiantservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pfe.example.etudiantservice.entities.Document;
import pfe.example.etudiantservice.enumerateur.TypeDocument;
import pfe.example.etudiantservice.service.DocumentService;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Upload un document
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("etudiantId") Long etudiantId,
            @RequestParam("type") TypeDocument type,
            @RequestParam("file") MultipartFile file
    ) {
        log.info("📤 Upload request - Candidat: {}, Type: {}, File: {}",
                etudiantId, type, file.getOriginalFilename());

        try {
            Document document = documentService.uploadDocument(etudiantId, type, file);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Document uploadé avec succès");
            response.put("document", document);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("❌ Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("❌ Upload error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du téléchargement: " + e.getMessage()));
        }
    }

    /**
     * Récupérer tous les documents d'un étudiant
     */
    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<Document>> getDocumentsByEtudiant(
            @PathVariable Long etudiantId
    ) {
        log.info("📋 Fetching documents for etudiant {}", etudiantId);
        List<Document> documents = documentService.getDocumentsByEtudiant(etudiantId);
        return ResponseEntity.ok(documents);
    }

    /**
     * Télécharger un document
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        try {
            Document document = documentService.getDocumentById(id);
            Path filePath = documentService.getFilePath(document.getCheminFichier());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                log.error("❌ File not found or not readable: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + document.getNomFichier() + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("❌ Download error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Supprimer un document
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok(Map.of("message", "Document supprimé avec succès"));
        } catch (Exception e) {
            log.error("❌ Delete error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Valider/Rejeter un document
     */
    @PutMapping("/{id}/validate")
    public ResponseEntity<?> validateDocument(
            @PathVariable Long id,
            @RequestParam boolean isValid,
            @RequestParam(required = false) String commentaire
    ) {
        try {
            Document document = documentService.validateDocument(id, isValid, commentaire);
            return ResponseEntity.ok(Map.of(
                    "message", isValid ? "Document validé" : "Document rejeté",
                    "document", document
            ));
        } catch (Exception e) {
            log.error("❌ Validation error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
