package pfe.example.etudiantservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pfe.example.etudiantservice.entities.Document;
import pfe.example.etudiantservice.enumerateur.TypeDocument;
import pfe.example.etudiantservice.service.DocumentService;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("etudiantId") Long etudiantId,
            @RequestParam("type") TypeDocument type,
            @RequestParam("file") MultipartFile file
    ) {
        log.info("📤 Upload request - Etudiant: {}, Type: {}, File: {}",
                etudiantId, type, file.getOriginalFilename());

        try {
            Document document = documentService.uploadDocument(etudiantId, type, file);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Document uploadé avec succès vers Nextcloud");
            response.put("document", document);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            System.out.println("❌ Validation error: {}"+ e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            System.out.println("❌ Upload error: "+ e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du téléchargement: " + e.getMessage()));
        }
    }

    // NOUVELLE MÉTHODE : Télécharger un document depuis Nextcloud
    @GetMapping("/download/{documentId}")
    public ResponseEntity<?> downloadDocument(@PathVariable Long documentId) {
        try {
            Document document = documentService.getDocumentById(documentId);
            InputStream inputStream = documentService.downloadDocument(documentId);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + document.getNomFichier() + "\"");
            headers.setContentType(MediaType.parseMediaType(document.getContentType()));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));

        } catch (Exception e) {
            log.error("❌ Download error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du téléchargement"));
        }
    }

    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<Document>> getDocumentsByEtudiant(@PathVariable Long etudiantId) {
        List<Document> documents = documentService.getDocumentsByEtudiant(etudiantId);
        return ResponseEntity.ok(documents);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long documentId) {
        try {
            documentService.deleteDocument(documentId);
            return ResponseEntity.ok(Map.of("message", "Document supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}