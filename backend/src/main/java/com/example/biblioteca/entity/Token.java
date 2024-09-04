package com.example.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Builder(toBuilder = true)
@Table(name = "token")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String token;

    // Cuando se crea, expira y se confirmo el token

    @Column(name = "createdAt")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;


    @Column(name = "expiresAt")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expiresAt;

    @Column(name = "confirmedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime confirmedAt;

    @Column(name= "refreshedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime refreshedAt;

    @Column(name= "refreshedToken")
    @Temporal(TemporalType.TIMESTAMP)
    private String refreshedToken;
    // Un usuario puede tener muchos tokens a lo largo del tiempo

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public String getRefreshedToken() {
        return refreshedToken;
    }

    public void setRefreshedToken(String refreshedToken) {
        this.refreshedToken = refreshedToken;
    }

    public LocalDateTime getRefreshedAt() {
        return refreshedAt;
    }

    public void setRefreshedAt(LocalDateTime refreshedAt) {
        this.refreshedAt = refreshedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
