package pfe.example.etudiantservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pfe.example.etudiantservice.config.NextcloudClient;
import pfe.example.etudiantservice.entities.Document;
import pfe.example.etudiantservice.entities.Etudiant;
import pfe.example.etudiantservice.enumerateur.TypeDocument;
import pfe.example.etudiantservice.repositories.DocumentRepository;
import pfe.example.etudiantservice.repositories.EtudiantRepository;
import java.io.IOException;
import java.io.InputStream;
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
    private final NextcloudClient nextcloudClient;

    @Value("${nextcloud.documents-folder}")
    private String documentsFolder;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );
    public Document uploadDocument(
            Long etudiantId,
            TypeDocument type,
            MultipartFile file
    ) throws IOException {

        log.info("📤 Uploading document for etudiant {} - Type: {}", etudiantId, type);

        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'ID: " + etudiantId));

        validateFile(file, type);

        String etudiantFolder = documentsFolder + "/etudiant_" + etudiantId;
        nextcloudClient.createFolderIfNotExists(etudiantFolder);

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = getFileExtension(file.getOriginalFilename());
        String uniqueFileName = type.name() + "_" + timestamp + "_" +
                UUID.randomUUID().toString().substring(0, 8) + extension;

        String nextcloudPath = etudiantFolder + "/" + uniqueFileName;

        log.info("🔍 Full Nextcloud path: {}", nextcloudPath);

        // ✅ SOLUTION : Convertir en byte[] AVANT d'uploader
        try {
            byte[] fileBytes = file.getBytes();
            System.out.println("📦 File converted to byte[]: " + fileBytes.length + " bytes");

            nextcloudClient.uploadFile(nextcloudPath, fileBytes);

        } catch (IOException e) {
            log.error("❌ Upload failed: {}", e.getMessage(), e);
            throw new IOException("Échec de l'upload vers Nextcloud: " + e.getMessage());
        }

        log.info("✅ File uploaded to Nextcloud: {}", nextcloudPath);

        Document document = Document.builder()
                .type(type)
                .nomFichier(file.getOriginalFilename())
                .cheminFichier(nextcloudPath)
                .contentType(file.getContentType())
                .tailleFichier(file.getSize())
                .isValidated(false)
                .etudiant(etudiant)
                .build();

        Document saved = documentRepository.save(document);
        log.info("✅ Document saved in DB - ID: {}, Type: {}", saved.getId(), type);

        return saved;
    }
    public InputStream downloadDocument(Long documentId) throws IOException {
        Document document = getDocumentById(documentId);
        log.info("📥 Downloading document {} from Nextcloud", documentId);
        return nextcloudClient.downloadFile(document.getCheminFichier());
    }

    public List<Document> getDocumentsByEtudiant(Long etudiantId) {
        log.info("📋 Fetching documents for etudiant {}", etudiantId);
        return documentRepository.findByEtudiantId(etudiantId);
    }

    public Document getDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé avec l'ID: " + documentId));
    }

    public void deleteDocument(Long documentId) throws IOException {
        Document document = getDocumentById(documentId);

        try {
            nextcloudClient.deleteFile(document.getCheminFichier());
            log.info("🗑️ File deleted from Nextcloud: {}", document.getCheminFichier());
        } catch (IOException e) {
            log.warn("⚠️ Failed to delete file from Nextcloud: {}", e.getMessage());
        }

        documentRepository.delete(document);
        log.info("🗑️ Document deleted from DB: {}", documentId);
    }

    public Document validateDocument(Long documentId, boolean isValid, String commentaire) {
        Document document = getDocumentById(documentId);
        document.setIsValidated(isValid);
        document.setCommentaireValidation(commentaire);

        Document saved = documentRepository.save(document);
        log.info("✅ Document {} - {}: {}",
                documentId,
                isValid ? "validé" : "rejeté",
                commentaire);

        return saved;
    }

    private void validateFile(MultipartFile file, TypeDocument type) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    String.format("Fichier trop volumineux (%.2f MB). Maximum autorisé: 10 MB",
                            file.getSize() / (1024.0 * 1024.0))
            );
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw new IllegalArgumentException("Nom de fichier invalide");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Type de fichier invalide");
        }

        boolean isValidType = false;
        String allowedFormats = "";

        switch (type) {
            case CARTE_IDENTITE:
            case CERTIFICAT_NAISSANCE:
                isValidType = ALLOWED_IMAGE_TYPES.contains(contentType) ;
                allowedFormats = "JPEG, PNG, GIF";
                break;

            case DIPLOME_BAC:
            case DIPLOME_LICENCE:
            case DIPLOME_MASTER:
            case RELEVE_NOTES:
            case AUTRE:
                isValidType = ALLOWED_IMAGE_TYPES.contains(contentType) ;
                allowedFormats = "JPEG, PNG, GIF";
                break;
        }

        if (!isValidType) {
            throw new IllegalArgumentException(
                    String.format("Format non supporté pour '%s'. Formats acceptés: %s",
                            type, allowedFormats)
            );
        }

        log.info("✅ File validation passed - Type: {}, Size: {} bytes", type, file.getSize());
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}