package com.example.biblioteca.service;

import com.example.biblioteca.entity.Token;
import com.example.biblioteca.entity.User;
import com.example.biblioteca.repository.TokenRepo;
import com.example.biblioteca.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TokenService {

    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;


    @Autowired
    private TokenRepo tokenRepository;
    @Autowired
    private JwtService jwtService;

    public Token createRefreshToken(User user) {
        Token refreshToken = Token.builder()
                .token(jwtService.getToken(user))
                .user(user)
                .createdAt(LocalDateTime.now())
                .refreshedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();
        System.out.print("token nuevo"+refreshToken );
        System.out.print(refreshTokenDurationMs + "minutos duracion");
        return refreshToken;
                //tokenRepository.save(refreshToken);
    }

    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void verifyExpiration(Token token) {
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            throw new RuntimeException("Refresh token expiró. Inicie sesión nuevamente.");
        }
    }
    public void deleteByUser(User user) {
        tokenRepository.deleteByUser(user);
    }
}
