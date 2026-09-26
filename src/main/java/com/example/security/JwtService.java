package com.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-seconds:3600}")
    private long expirationSeconds;

    private SecretKey key;

    @PostConstruct
    public void init() {
        // ensure secret key is of adequate length; decode if base64 provided
        byte[] keyBytes = jwtSecret.getBytes();
        if (jwtSecret.matches("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+$")) {
            // looks like JWT format — fallback (not expected)
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        }
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        return generateToken(username, expirationSeconds);
    }

    public String generateToken(String username, long customExpirationSeconds) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + (customExpirationSeconds * 1000));
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String extractUsername(String token) {
        Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return jws.getBody().getSubject();
    }

    public long extractExpirationMillis(String token) {
        Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        Date exp = jws.getBody().getExpiration();
        return exp.getTime();
    }
}

