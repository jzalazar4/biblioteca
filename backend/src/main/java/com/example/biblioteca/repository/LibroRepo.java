package com.example.biblioteca.repository;

import com.example.biblioteca.entity.Libro;
import com.example.biblioteca.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@EnableJpaRepositories
@Repository
public interface LibroRepo extends JpaRepository<Libro, Long> {

    List<Libro> findByTituloContainingIgnoreCase(String titulo);
    List<Libro> findByGeneroContainingIgnoreCase(String genero);
    List<Libro> findByAutorContainingIgnoreCase(String autor);
    List<Libro> findByRatingGreaterThanEqual(float rating);
    List<Libro> findByStock(Long stock);
    List<Libro> findAllByEstadoNot(String estado);
}
