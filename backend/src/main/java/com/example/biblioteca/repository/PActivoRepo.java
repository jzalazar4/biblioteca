package com.example.biblioteca.repository;

import com.example.biblioteca.entity.Libro;
import com.example.biblioteca.entity.PActivo;
import com.example.biblioteca.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@EnableJpaRepositories
@Repository
public interface PActivoRepo extends JpaRepository<PActivo, Long> {

    List<PActivo> findByLibroAndEstado(Libro libro, String estado);
    List<PActivo> findByUserId(Long userId);
    List<PActivo> findByUser(User user);
    List<PActivo> findByUserAndEstado(User user, String estado);
    PActivo findByLibroAndUserAndEstado(Libro libro, User user, String estado);

}
