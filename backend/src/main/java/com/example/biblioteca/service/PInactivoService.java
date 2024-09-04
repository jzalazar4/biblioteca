package com.example.biblioteca.service;

import com.example.biblioteca.entity.PActivo;
import com.example.biblioteca.entity.PInactivo;
import com.example.biblioteca.repository.PActivoRepo;
import com.example.biblioteca.repository.PInactivoRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PInactivoService {

    @Autowired
    public PInactivoRepo repo;
    @Autowired
    public PActivoRepo activoRepo;



    public List<PInactivo> verPrestamos() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(PInactivo::getId))
                .collect(Collectors.toList());
    }

    public Optional<PInactivo> unPrestamo(Long id) {
        return repo.findById(id);
    }

    public void eliminarP(Long id) {
        repo.deleteById(id);
    }

    public ResponseEntity<PInactivo> actualizarP(Long id, PInactivo p) {
        Optional<PInactivo> prestamoOptional = repo.findById(id);

        if (prestamoOptional.isPresent()) {
            PInactivo prestamoExistente = prestamoOptional.get();

            // actualiza el libro
            if (p.getLibro() != null && p.getLibro().getId() != null) {
                prestamoExistente.setLibro(p.getLibro());
            }

            if (p.getUser() != null && p.getUser().getId() != null) {
                prestamoExistente.setUser(p.getUser());
            }

            if (p.getFinicio() != null) {
                prestamoExistente.setFinicio(p.getFinicio());
            }
            if (p.getFfin() != null) {
                prestamoExistente.setFfin(p.getFfin());
            }

            PInactivo prestamoActualizado = repo.save(prestamoExistente);
            return ResponseEntity.ok(prestamoActualizado);
        } else {

            return ResponseEntity.notFound().build();
        }
    }

}
