package pfe.example.etudiantservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pfe.example.etudiantservice.entities.Document;
import pfe.example.etudiantservice.enumerateur.TypeDocument;
import pfe.example.etudiantservice.service.DocumentService;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/documents")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final Path documentsPath = Paths.get("src/main/resources/static/documents");

    // Upload un document
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("candidatId") Long candidatId,
            @RequestParam("type") TypeDocument type,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            Document document = documentService.uploadDocument(candidatId, type, file);
            return ResponseEntity.ok(document);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erreur lors du téléchargement: " + e.getMessage());
        }
    }

    // Récupérer tous les documents d'un candidat
    @GetMapping("/candidat/{candidatId}")
    public ResponseEntity<List<Document>> getDocumentsByCandidat(
            @PathVariable Long candidatId
    ) {
        List<Document> documents = documentService.getDocumentsByCandidat(candidatId);
        return ResponseEntity.ok(documents);
    }

    // Servir/télécharger un document (comme votre ImageController)
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveDocument(@PathVariable String filename) {
        try {
            Path file = documentsPath.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Déterminer le type de contenu
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Télécharger un document (force download)
    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        try {
            Document document = documentService.getDocumentById(documentId);
            Path file = documentsPath.resolve(document.getCheminFichier());
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(document.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + document.getNomFichier() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer un document
    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long documentId) {
        try {
            documentService.deleteDocument(documentId);
            return ResponseEntity.ok("Document supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erreur lors de la suppression: " + e.getMessage());
        }
    }
}