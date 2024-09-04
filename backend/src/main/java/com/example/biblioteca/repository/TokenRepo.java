package com.example.biblioteca.repository;

import com.example.biblioteca.entity.Token;
import com.example.biblioteca.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@EnableJpaRepositories
@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);
    Optional<Token> findByUser(User user);

    void deleteByUser(User user);

}
