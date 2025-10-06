package br.tec.omny.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret:mySecretKey}")
    private String secret;
    
    @Value("${jwt.expiration:86400}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    /**
     * Gera um token JWT
     * @param claims Claims do token
     * @return Token JWT
     */
    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Gera um token JWT com dados do usuário
     * @param userId ID do usuário
     * @param admin Se é admin
     * @param staffId ID do staff
     * @param firstName Primeiro nome
     * @param lastName Último nome
     * @param phoneNumber Telefone
     * @return Token JWT
     */
    public String generateToken(Integer userId, Integer admin, Integer staffId, 
                               String firstName, String lastName, String phoneNumber) {
        Map<String, Object> claims = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        
        user.put("user_id", userId);
        user.put("admin", admin);
        user.put("staffid", staffId);
        user.put("firstname", firstName);
        user.put("lastname", lastName);
        user.put("phonenumber", phoneNumber);
        
        claims.put("user", user);
        
        return generateToken(claims);
    }
    
    /**
     * Extrai claims do token
     * @param token Token JWT
     * @return Claims do token
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Verifica se o token é válido
     * @param token Token JWT
     * @return true se válido, false caso contrário
     */
    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Verifica se o token expirou
     * @param token Token JWT
     * @return true se expirado, false caso contrário
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
