package pfe.example.etudiantservice.service;


import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Extrait le userId du token JWT
     */
    public String getUserIdFromToken(String token) {
        try {
            token = cleanToken(token);

            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();

        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expiré: {}", e.getMessage());
            throw new RuntimeException("Token expiré", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT non supporté: {}", e.getMessage());
            throw new RuntimeException("Token invalide", e);
        } catch (MalformedJwtException e) {
            logger.error("Token JWT malformé: {}", e.getMessage());
            throw new RuntimeException("Token malformé", e);
        } catch (SignatureException e) {
            logger.error("Signature JWT invalide: {}", e.getMessage());
            throw new RuntimeException("Signature invalide", e);
        } catch (IllegalArgumentException e) {
            logger.error("Token JWT vide: {}", e.getMessage());
            throw new RuntimeException("Token vide", e);
        }
    }

    /**
     * Extrait le rôle du token
     */
    public String getRoleFromToken(String token) {
        try {
            token = cleanToken(token);

            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("role", String.class);

        } catch (Exception e) {
            logger.error("Erreur extraction rôle: {}", e.getMessage());
            throw new RuntimeException("Impossible d'extraire le rôle", e);
        }
    }

    /**
     * Extrait l'email du token
     */
    public String getEmailFromToken(String token) {
        try {
            token = cleanToken(token);

            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("email", String.class);

        } catch (Exception e) {
            logger.error("Erreur extraction email: {}", e.getMessage());
            throw new RuntimeException("Impossible d'extraire l'email", e);
        }
    }

    /**
     * Extrait le departementId du token (peut être null)
     */
    public String getDepartementIdFromToken(String token) {
        try {
            token = cleanToken(token);

            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("departementId", String.class);

        } catch (Exception e) {
            logger.warn("Pas de departementId dans le token (normal pour étudiants)");
            return null;
        }
    }

    /**
     * Valide le token JWT
     */
    public boolean validateToken(String token) {
        try {
            token = cleanToken(token);

            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token);

            return true;

        } catch (ExpiredJwtException e) {
            logger.error("Token expiré");
            return false;
        } catch (Exception e) {
            logger.error("Token invalide: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Vérifie si le token est expiré
     */
    public boolean isTokenExpired(String token) {
        try {
            token = cleanToken(token);

            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            return expiration.before(new Date());

        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Extrait tous les claims du token
     */
    public Claims getAllClaimsFromToken(String token) {
        token = cleanToken(token);

        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Nettoie le token (retire "Bearer " si présent)
     */
    private String cleanToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token vide");
        }

        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        return token;
    }

    /**
     * Génère un token JWT (UNIQUEMENT dans Auth-Service)
     * Les autres services n'ont PAS besoin de cette méthode
     */
    public String generateToken(String userId, String email, String role, String departementId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        JwtBuilder builder = Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret);

        // Ajouter departementId seulement si non null
        if (departementId != null) {
            builder.claim("departementId", departementId);
        }

        return builder.compact();
    }
}