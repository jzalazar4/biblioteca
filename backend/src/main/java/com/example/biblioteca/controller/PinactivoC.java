package com.example.biblioteca.controller;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import com.example.biblioteca.entity.PActivo;
import com.example.biblioteca.entity.PInactivo;
import com.example.biblioteca.repository.PInactivoRepo;
import com.example.biblioteca.service.PInactivoService;
import com.example.biblioteca.service.csv.ExportCsv;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/inactivo")
public class PinactivoC {

    private final PInactivoRepo repo;
    private final PInactivoService service;

    @Autowired
    private ExportCsv exportCsv;

    // VER TODOS
    @GetMapping(path = "/list")
    public List<PInactivo> verPrestamos() {
        return service.verPrestamos();
    }

    // OBTENER UNO
    @GetMapping(path = "/{id}")
    public Optional<PInactivo> unPrestamo(@PathVariable Long id) {
        return service.unPrestamo(id);
    }

    // ELIMINAR
    @DeleteMapping(path = "/delete/{id}")
    public void eliminarP(@PathVariable Long id) {
        service.eliminarP(id);
    }

    // ACTUALIZAR
    @PutMapping("/update/{id}")
    public ResponseEntity<PInactivo> actualizarPrestamo(@PathVariable Long id,
                                                        @RequestBody PInactivo p) {
        try {
            // Parsear fechas antes de asignarlas
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

            if (p.getFinicio() != null) {
                Date parsedFinicio = dateFormat.parse(dateFormat.format(p.getFinicio()));
                p.setFinicio(parsedFinicio);
            }

            if (p.getFfin() != null) {
                Date parsedFfin = dateFormat.parse(dateFormat.format(p.getFfin()));
                p.setFfin(parsedFfin);
            }

            ResponseEntity<PInactivo> response = service.actualizarP(id, p);
            return response;
        } catch (ParseException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // EXPORTAR CSV
    @GetMapping("/exportCsv")
    public void exportInactivoCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"inactivos-biblioteca.csv\"");

        exportCsv.PInactivoCsv(service.verPrestamos(), response.getOutputStream());
    }
}
