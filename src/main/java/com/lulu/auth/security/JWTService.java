package com.lulu.auth.security;

import com.lulu.auth.model.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JWTService {

    private final String SECRET_KEY_STRING = "3S!g#tZfQd@8LpW&vCk$R9mXn#Ye^bTu"; // mínimo 256 bits (~32 caracteres)

    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    public String generateToken(UserModel user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRol().getTipoRol())
                .claim("user_id", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 horas
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, UserModel user) {
        return user.getUsername().equals(extractUsername(token)) && !isTokenExpired(token);
    }
    public Long extractUserId(String token) {
        Claims claims = extractClaims(token);
        return claims.get("user_id", Long.class);
    }

}
