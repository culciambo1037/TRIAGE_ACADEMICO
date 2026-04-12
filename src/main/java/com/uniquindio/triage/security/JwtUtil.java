package com.uniquindio.triage.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Clave secreta — en producción debe estar en variables de entorno
    private static final String SECRET = "triage_academico_uniquindio_secret_key_2025";
    private static final long EXPIRATION_MS = 86400000; // 24 horas

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Genera un token JWT para un usuario
    public String generarToken(String correo, String rol) {
        return Jwts.builder()
            .setSubject(correo)
            .claim("rol", rol)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    // Extrae el correo del token
    public String extraerCorreo(String token) {
        return extraerClaims(token).getSubject();
    }

    // Extrae el rol del token
    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    // Valida que el token sea correcto y no haya expirado
    public boolean validarToken(String token) {
        try {
            extraerClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extrae todos los claims del token
    private Claims extraerClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}