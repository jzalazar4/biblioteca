package com.example.biblioteca.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Service;
import java.security.Key;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "448a330a3e6b51970a4f789b9378c05fd2892e05ee05fe1522657503ba235600";

    private static final int LOGIN_EXP = 15; // cuando el user inicia sesion tiene un token de 15 min
    private static final int REFRESH_EXP = 60; // si esta por expirar y extiende la sesion el token nuevo es de 60

    public String getToken(UserDetails user) {

        return getToken(new HashMap<>(), user, LOGIN_EXP);
    }

    public String getRefreshToken(UserDetails user) {
        return getToken(new HashMap<>(), user, REFRESH_EXP);
    }

    private String getToken(Map<String, Object> extraClaims, UserDetails user, int expMinutes) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // varia: login o refresh u otro
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expMinutes))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
/*
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT expiro", e);
        } catch (JwtException e) {
            throw new RuntimeException("JWT inválido", e);
        }
    }*/
public boolean validateToken(String authToken) {
    try {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .setAllowedClockSkewSeconds(60) // Permite una desviación de tiempo si es necesario
                .build();
        jwtParser.parseClaimsJws(authToken);
        return true;
    } catch (ExpiredJwtException e) {
        System.err.println("Token expirado. Tiempo de expiración: " + e.getClaims().getExpiration());
        throw new RuntimeException("JWT expirado", e);
    } catch (JwtException e) {
        throw new RuntimeException("JWT inválido", e);
    }
}

    private Claims getAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }
}