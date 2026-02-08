//package pfe.example.etudiantservice.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.JwtException;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
//import org.springframework.stereotype.Component;
//
//
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.time.Instant;
//
//@Component
//@Slf4j
//public class JwtTokenProvider {
//
//    private final JwtDecoder jwtDecoder;
//
//    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecret) {
//        // Créer le décodeur avec la clé secrète
//        SecretKeySpec secretKey = new SecretKeySpec(
//                jwtSecret.getBytes(StandardCharsets.UTF_8),
//                "HmacSHA256"
//        );
//        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).build();
//    }
//
//    /**
//     * Extraire le login (subject) du JWT
//     */
//    public String getLoginFromToken(String token) {
//        try {
//            Jwt jwt = decodeToken(token);
//            return jwt.getSubject();  // C'est le login de l'utilisateur
//        } catch (Exception e) {
//            log.error("Erreur extraction login: {}", e.getMessage());
//            throw new InvalidBearerTokenException("Impossible d'extraire le login", e);
//        }
//    }
//
//    /**
//     * Extraire le scope (rôles) du JWT
//     * Format: "ROLE_ETUDIANT ROLE_USER" ou "ROLE_ADMIN"
//     */
//    public String getScopeFromToken(String token) {
//        try {
//            Jwt jwt = decodeToken(token);
//            return jwt.getClaimAsString("scope");
//        } catch (Exception e) {
//            log.error("Erreur extraction scope: {}", e.getMessage());
//            throw new InvalidBearerTokenException("Impossible d'extraire le scope", e);
//        }
//    }
//
//    /**
//     * Vérifier si l'utilisateur a le rôle ETUDIANT
//     */
//    public boolean hasEtudiantRole(String token) {
//        String scope = getScopeFromToken(token);
//        return scope != null && scope.contains("ROLE_ETUDIANT");
//    }
//
//    /**
//     * Vérifier si l'utilisateur a le rôle ADMIN
//     */
//    public boolean hasAdminRole(String token) {
//        String scope = getScopeFromToken(token);
//        return scope != null && scope.contains("ROLE_ADMIN");
//    }
//
//    /**
//     * Valider le token JWT
//     */
//    public boolean validateToken(String token) {
//        try {
//            Jwt jwt = decodeToken(token);
//
//            // Vérifier l'expiration
//            Instant expiresAt = jwt.getExpiresAt();
//            if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
//                log.warn("Token expiré");
//                return false;
//            }
//
//            return true;
//        } catch (JwtException e) {
//            log.error("Token invalide: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    /**
//     * Vérifier si le token est expiré
//     */
//    public boolean isTokenExpired(String token) {
//        try {
//            Jwt jwt = decodeToken(token);
//            Instant expiresAt = jwt.getExpiresAt();
//            return expiresAt != null && expiresAt.isBefore(Instant.now());
//        } catch (Exception e) {
//            return true;
//        }
//    }
//
//    /**
//     * Décoder le token JWT (méthode privée)
//     */
//    private Jwt decodeToken(String token) {
//        token = cleanToken(token);
//
//        try {
//            return jwtDecoder.decode(token);
//        } catch (JwtException e) {
//            log.error("Erreur décodage JWT: {}", e.getMessage());
//            throw new InvalidBearerTokenException("Token invalide", e);
//        }
//    }
//
//    /**
//     * Nettoyer le token (retirer "Bearer " si présent)
//     */
//    private String cleanToken(String token) {
//        if (token == null || token.isEmpty()) {
//            throw new IllegalArgumentException("Token vide");
//        }
//
//        if (token.startsWith("Bearer ")) {
//            return token.substring(7);
//        }
//
//        return token;
//    }
//
//    /**
//     * Obtenir le JWT décodé complet
//     */
//    public Jwt getDecodedJwt(String token) {
//        return decodeToken(token);
//    }
//}