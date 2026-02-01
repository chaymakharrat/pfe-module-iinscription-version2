package pfe.example.enrollement_module.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pfe.example.enrollement_module.entities.DemandeInscription;
import pfe.example.enrollement_module.entities.Document;
import pfe.example.enrollement_module.enumerateur.TypeDocument;
import pfe.example.enrollement_module.repository.DemandeInscriptionRepository;
import pfe.example.enrollement_module.repository.DocumentRepository;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DemandeInscriptionRepository demandeInscriptionRepository;

    // Chemin de stockage des documents
    private final Path documentsPath = Paths.get("src/main/resources/static/documents");

    public Document uploadDocument(
            Long demandeInscriptionId,
            TypeDocument type,
            MultipartFile file
    ) throws IOException {

        // Vérifier que le candidat existe
        DemandeInscription demandeInscription = demandeInscriptionRepository.findById(demandeInscriptionId)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé"));

        // Validation du fichier
        validateFile(file, type);

        // Créer le dossier s'il n'existe pas
        Files.createDirectories(documentsPath);

        // Générer un nom unique
        String extension = getFileExtension(file.getOriginalFilename());
        String uniqueFileName = demandeInscriptionId + "_" + type.name() + "_" +
                UUID.randomUUID().toString() + extension;

        // Sauvegarder le fichier
        Path filePath = documentsPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Créer l'entité Document
        Document document = Document.builder()
                .type(type)
                .nomFichier(file.getOriginalFilename())
                .cheminFichier(uniqueFileName)  // Seulement le nom du fichier
                .contentType(file.getContentType())
                .tailleFichier(file.getSize())
                //.dateTelechargement(LocalDateTime.now())
                //.statut(StatutDocument.EN_ATTENTE)
                .candidat(demandeInscription)
                .build();

        return documentRepository.save(document);
    }

    public List<Document> getDocumentsByCandidat(Long candidatId) {
        return documentRepository.findByCandidatId(candidatId);
    }

    public Document getDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));
    }

    public void deleteDocument(Long documentId) throws IOException {
        Document document = getDocumentById(documentId);

        // Supprimer le fichier physique
        Path filePath = documentsPath.resolve(document.getCheminFichier());
        Files.deleteIfExists(filePath);

        // Supprimer de la base de données
        documentRepository.delete(document);
    }

    private void validateFile(MultipartFile file, TypeDocument type) {
        // Vérifier que le fichier n'est pas vide
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        // Vérifier la taille (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Fichier trop volumineux (max 5MB)");
        }

        // Vérifier le type de fichier
        String contentType = file.getContentType();
        if (type == TypeDocument.PHOTO_IDENTITE) {
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("La photo doit être une image");
            }
        } else {
            if (contentType == null ||
                    (!contentType.equals("application/pdf") && !contentType.startsWith("image/"))) {
                throw new IllegalArgumentException("Format non supporté. Utilisez PDF ou image");
            }
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}