package pfe.example.etudiantservice.config;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class NextcloudClient {

    @Value("${nextcloud.url}")
    private String nextcloudUrl;

    @Value("${nextcloud.username}")
    private String username;

    @Value("${nextcloud.password}")
    private String password;

    @Value("${nextcloud.base-path}")
    private String basePath;

    @Value("${nextcloud.documents-folder}")
    private String documentsFolder;

    private Sardine sardine;
    private String baseWebDavUrl;

//    @PostConstruct
//    public void init() {
//        this.sardine = SardineFactory.begin(username, password);
//        this.baseWebDavUrl = nextcloudUrl + basePath;
//        log.info("✅ Nextcloud client initialized - URL: {}", baseWebDavUrl);
//
//        try {
//            createFolderIfNotExists(documentsFolder);
//        } catch (IOException e) {
//            log.error("❌ Failed to create base folder", e);
//        }
//    }
@PostConstruct
public void init() {
    // ✅ Configurer Sardine avec des paramètres custom
    org.apache.http.impl.client.HttpClientBuilder httpClientBuilder =
            org.apache.http.impl.client.HttpClientBuilder.create();

    // Désactiver les retries automatiques
    httpClientBuilder.disableAutomaticRetries();

    this.sardine = new com.github.sardine.impl.SardineImpl(
            httpClientBuilder,
            username,
            password
    );

    this.baseWebDavUrl = nextcloudUrl + basePath;
    System.out.println("✅ Nextcloud client initialized - URL: " + baseWebDavUrl);

    try {
        createFolderIfNotExists(documentsFolder);
    } catch (IOException e) {
        System.err.println("❌ Failed to create base folder: " + e.getMessage());
    }
}

    // ✅ NOUVELLE MÉTHODE avec byte[]
    public String uploadFile(String relativePath, byte[] fileContent) throws IOException {
        String fullPath = baseWebDavUrl + "/" + relativePath;

        System.out.println("===========================================");
        System.out.println("📤 UPLOADING FILE (byte[] version)");
        System.out.println("Full Path: " + fullPath);
        System.out.println("File Size: " + fileContent.length + " bytes");
        System.out.println("===========================================");

        // Vérifier que le dossier parent existe
        String parentFolder = relativePath.substring(0, relativePath.lastIndexOf('/'));
        createFolderIfNotExists(parentFolder);

        try {
            // ✅ Créer un ByteArrayInputStream qui peut être relu
            ByteArrayInputStream bais = new ByteArrayInputStream(fileContent);
            sardine.put(fullPath, bais, "application/octet-stream", false, (long) fileContent.length);
            System.out.println("✅ UPLOAD SUCCESS!");
            return relativePath;
        } catch (IOException e) {
            System.err.println("❌ UPLOAD FAILED: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Nextcloud upload failed to " + fullPath + ": " + e.getMessage(), e);
        }
    }

    public InputStream downloadFile(String relativePath) throws IOException {
        String fullPath = baseWebDavUrl + "/" + relativePath;

        log.info("📥 Downloading from Nextcloud: {}", fullPath);
        return sardine.get(fullPath);
    }

    public void deleteFile(String relativePath) throws IOException {
        String fullPath = baseWebDavUrl + "/" + relativePath;

        log.info("🗑️ Deleting from Nextcloud: {}", fullPath);
        sardine.delete(fullPath);
    }

    public void createFolder(String folderPath) throws IOException {
        String fullPath = baseWebDavUrl + "/" + folderPath;

        log.info("📁 Creating folder: {}", fullPath);
        sardine.createDirectory(fullPath);
    }

//    public void createFolderIfNotExists(String folderPath) throws IOException {
//        System.out.println("===========================================");
//        System.out.println("📁 CREATE FOLDER IF NOT EXISTS");
//        System.out.println("Folder Path: " + folderPath);
//        System.out.println("===========================================");
//
//        // Créer récursivement tous les dossiers parents
//        String[] folders = folderPath.split("/");
//        String currentPath = "";
//
//        for (String folder : folders) {
//            if (folder.isEmpty()) continue;
//
//            currentPath = currentPath.isEmpty() ? folder : currentPath + "/" + folder;
//            String fullPath = baseWebDavUrl + "/" + currentPath;
//
//            System.out.println("🔍 Checking: " + fullPath);
//
//            try {
//                boolean exists = sardine.exists(fullPath);
//                System.out.println("   EXISTS: " + exists);
//
//                if (!exists) {
//                    System.out.println("   📁 Creating: " + fullPath);
//                    sardine.createDirectory(fullPath);
//                    System.out.println("   ✅ Created!");
//                }
//            } catch (IOException e) {
//                System.err.println("   ❌ Error: " + e.getMessage());
//                throw e;
//            }
//        }
//
//        System.out.println("✅ All folders created/verified");
//    }
private final Set<String> createdFolders = new ConcurrentHashMap<String, Boolean>().keySet(true);

    public void createFolderIfNotExists(String folderPath) throws IOException {
        // ✅ Si déjà créé dans cette session, skip
        if (createdFolders.contains(folderPath)) {
            System.out.println("✅ Folder already created in this session: " + folderPath);
            return;
        }

        System.out.println("===========================================");
        System.out.println("📁 CREATE FOLDER IF NOT EXISTS");
        System.out.println("Folder Path: " + folderPath);
        System.out.println("===========================================");

        // Créer récursivement tous les dossiers parents
        String[] folders = folderPath.split("/");
        String currentPath = "";

        for (String folder : folders) {
            if (folder.isEmpty()) continue;

            currentPath = currentPath.isEmpty() ? folder : currentPath + "/" + folder;

            // ✅ Skip si déjà créé
            if (createdFolders.contains(currentPath)) {
                continue;
            }

            String fullPath = baseWebDavUrl + "/" + currentPath;

            System.out.println("🔍 Checking: " + fullPath);

            try {
                boolean exists = sardine.exists(fullPath);

                if (!exists) {
                    System.out.println("   📁 Creating: " + fullPath);
                    sardine.createDirectory(fullPath);
                    System.out.println("   ✅ Created!");
                } else {
                    System.out.println("   ✅ Already exists");
                }

                // ✅ Ajouter au cache
                createdFolders.add(currentPath);

            } catch (IOException e) {
                if (e.getMessage().contains("405") || e.getMessage().contains("423")) {
                    System.out.println("   ⚠️ Folder already exists (race condition)");
                    createdFolders.add(currentPath);
                } else {
                    System.err.println("   ❌ Error: " + e.getMessage());
                    throw e;
                }
            }
        }

        System.out.println("✅ All folders created/verified");
    }

    public boolean fileExists(String relativePath) throws IOException {
        String fullPath = baseWebDavUrl + "/" + relativePath;
        return sardine.exists(fullPath);
    }

}