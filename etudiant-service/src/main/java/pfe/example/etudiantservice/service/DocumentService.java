package pfe.example.etudiantservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pfe.example.etudiantservice.entities.Document;
import pfe.example.etudiantservice.entities.Etudiant;

import jakarta.annotation.PostConstruct;
import pfe.example.etudiantservice.enumerateur.TypeDocument;
import pfe.example.etudiantservice.repositories.DocumentRepository;
import pfe.example.etudiantservice.repositories.EtudiantRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final EtudiantRepository etudiantRepository;

    @Value("${file.upload-dir:./uploads/documents}")
    private String uploadDirectory;

    private Path documentsPath;

    // Types de fichiers autorisés
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );

    private static final List<String> ALLOWED_PDF_TYPES = Arrays.asList(
            "application/pdf"
    );

    @PostConstruct
    public void init() {
        try {
            this.documentsPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();
            Files.createDirectories(documentsPath);
            log.info("✅ Documents directory created/verified: {}", documentsPath);
        } catch (IOException e) {
            log.error("❌ Could not create documents directory!", e);
            throw new RuntimeException("Could not create documents directory!", e);
        }
    }

    public Document uploadDocument(
            Long etudiantId,
            TypeDocument type,
            MultipartFile file
    ) throws IOException {

        log.info("📤 Uploading document for etudiant {} - Type: {} ({})",
                etudiantId, type);

        // Vérifier que l'étudiant existe
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'ID: " + etudiantId));

        // Validation du fichier
        validateFile(file, type);

        // Créer le dossier de l'étudiant s'il n'existe pas
        Path etudiantFolder = documentsPath.resolve("etudiant_" + etudiantId);
        Files.createDirectories(etudiantFolder);

        // Générer un nom unique avec timestamp
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = getFileExtension(file.getOriginalFilename());
        String uniqueFileName = type.name() + "_" + timestamp + "_" +
                UUID.randomUUID().toString().substring(0, 8) + extension;

        // Sauvegarder le fichier
        Path filePath = etudiantFolder.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("✅ File saved: {}", filePath);

        // Créer l'entité Document
        Document document = Document.builder()
                .type(type)
                .nomFichier(file.getOriginalFilename())
                .cheminFichier("etudiant_" + etudiantId + "/" + uniqueFileName)
                .contentType(file.getContentType())
                .tailleFichier(file.getSize())
                .isValidated(false)
                .etudiant(etudiant)
                .build();

        Document saved = documentRepository.save(document);
        log.info("✅ Document saved in DB - ID: {}, Type: {}", saved.getId());

        return saved;
    }

    /**
     * Upload multiple documents
     */
    public List<Document> uploadDocuments(
            Long etudiantId,
            List<MultipartFile> files,
            List<TypeDocument> types
    ) throws IOException {

        if (files.size() != types.size()) {
            throw new IllegalArgumentException(
                    "Le nombre de fichiers doit correspondre au nombre de types"
            );
        }

        List<Document> uploadedDocuments = new java.util.ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            Document doc = uploadDocument(etudiantId, types.get(i), files.get(i));
            uploadedDocuments.add(doc);
        }

        return uploadedDocuments;
    }

    public List<Document> getDocumentsByEtudiant(Long etudiantId) {
        log.info("📋 Fetching documents for etudiant {}", etudiantId);
        return documentRepository.findByEtudiantId(etudiantId);
    }

//    public List<Document> getDocumentsByEtudiantAndType(Long etudiantId, TypeDocument type) {
//        log.info("📋 Fetching {} documents for etudiant {}", type.getLabel(), etudiantId);
//        return documentRepository.findByEtudiantIdAndType(etudiantId, type);
//    }

    public Document getDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé avec l'ID: " + documentId));
    }

    public void deleteDocument(Long documentId) throws IOException {
        Document document = getDocumentById(documentId);

        // Supprimer le fichier physique
        Path filePath = documentsPath.resolve(document.getCheminFichier());
        boolean deleted = Files.deleteIfExists(filePath);

        if (deleted) {
            log.info("🗑️ File deleted: {}", filePath);
        } else {
            log.warn("⚠️ File not found for deletion: {}", filePath);
        }

        // Supprimer de la base de données
        documentRepository.delete(document);
        log.info("🗑️ Document deleted from DB: {} ({})", documentId);
    }

    public Document validateDocument(Long documentId, boolean isValid, String commentaire) {
        Document document = getDocumentById(documentId);
        document.setIsValidated(isValid);
        document.setCommentaireValidation(commentaire);

        Document saved = documentRepository.save(document);
        log.info("✅ Document {} - {}: {}",
                isValid ? "validé" : "rejeté",
                commentaire);

        return saved;
    }

    public Path getFilePath(String cheminFichier) {
        return documentsPath.resolve(cheminFichier).normalize();
    }

    /**
     * Vérifier si tous les documents requis sont uploadés
     */
    public boolean hasAllRequiredDocuments(Long etudiantId) {
        List<Document> documents = getDocumentsByEtudiant(etudiantId);

        // Documents requis (ajustez selon vos besoins)
        List<TypeDocument> requiredTypes = Arrays.asList(
                TypeDocument.CARTE_IDENTITE,
                TypeDocument.DIPLOME_BAC
        );

        for (TypeDocument requiredType : requiredTypes) {
            boolean hasType = documents.stream()
                    .anyMatch(doc -> doc.getType() == requiredType);

            if (!hasType) {
                log.warn("⚠️ Missing required document: {} for etudiant {}",
                         etudiantId);
                return false;
            }
        }

        return true;
    }

    /**
     * Obtenir les documents manquants
     */
    public List<TypeDocument> getMissingDocuments(Long etudiantId) {
        List<Document> documents = getDocumentsByEtudiant(etudiantId);

        List<TypeDocument> requiredTypes = Arrays.asList(
                TypeDocument.CARTE_IDENTITE,
                TypeDocument.DIPLOME_BAC
        );

        return requiredTypes.stream()
                .filter(type -> documents.stream().noneMatch(doc -> doc.getType() == type))
                .toList();
    }

    private void validateFile(MultipartFile file, TypeDocument type) {
        // Vérifier que le fichier n'est pas vide
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        // Vérifier la taille (max 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    String.format("Fichier trop volumineux (%.2f MB). Maximum autorisé: 10 MB",
                            file.getSize() / (1024.0 * 1024.0))
            );
        }

        // Vérifier le nom du fichier
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw new IllegalArgumentException("Nom de fichier invalide");
        }

        // Vérifier le type de fichier
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Type de fichier invalide");
        }

        // Validation selon le type de document
        boolean isValidType = false;
        String allowedFormats = "";

        switch (type) {
            case CARTE_IDENTITE:
            case CERTIFICAT_NAISSANCE:
                // Images ou PDF
                isValidType = ALLOWED_IMAGE_TYPES.contains(contentType) ||
                        ALLOWED_PDF_TYPES.contains(contentType);
                allowedFormats = "PDF, JPEG, PNG, GIF";
                break;

            case DIPLOME_BAC:
            case DIPLOME_LICENCE:
            case DIPLOME_MASTER:
            case RELEVE_NOTES:
                // Uniquement PDF
                isValidType = ALLOWED_PDF_TYPES.contains(contentType);
                allowedFormats = "PDF uniquement";
                break;

            case AUTRE:
                // PDF ou images
                isValidType = ALLOWED_IMAGE_TYPES.contains(contentType) ||
                        ALLOWED_PDF_TYPES.contains(contentType);
                allowedFormats = "PDF, JPEG, PNG, GIF";
                break;
        }

        if (!isValidType) {
            throw new IllegalArgumentException(
                    String.format("Format non supporté pour '%s'. Formats acceptés: %s",
                             allowedFormats)
            );
        }

        log.info("✅ File validation passed - Type: {}, Size: {} bytes, ContentType: {}",
                 file.getSize(), contentType);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public String getReadableFileSize(long size) {
        if (size <= 0) return "0 B";

        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return String.format("%.1f %s",
                size / Math.pow(1024, digitGroups),
                units[digitGroups]);
    }
}