package com.example.biblioteca.repository;

import com.example.biblioteca.entity.Libro;
import com.example.biblioteca.entity.PActivo;
import com.example.biblioteca.entity.PInactivo;
import com.example.biblioteca.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories
@Repository
public interface PInactivoRepo extends JpaRepository<PInactivo, Long> {
    List<PInactivo> findByLibro(Libro libro);
    List<PInactivo> findByUser(User user);
}
