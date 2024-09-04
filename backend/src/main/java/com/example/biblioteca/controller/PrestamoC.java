package com.example.biblioteca.controller;

import com.example.biblioteca.entity.PActivo;
import com.example.biblioteca.repository.PActivoRepo;
import com.example.biblioteca.service.PAService;
import com.example.biblioteca.service.csv.ExportCsv;
import com.example.biblioteca.service.response.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/prestamo")
public class PrestamoC {

    public final PActivoRepo repo;
    public final PAService service;

    @Autowired
    private ExportCsv exportCsv;

    // GUARDAR
    @PostMapping("/save")
    public ResponseEntity<ErrorResponse> nuevoPrestamo(@RequestBody PActivo prestamo) {
        return service.nuevoPrestamo(prestamo);
    }

    // DEVOLVER LIBRO
    @PutMapping("/devolver/{id}")
    public ResponseEntity<String> devolverLibro(@PathVariable Long id) {
        try {
            service.devolverLibro(id);
            return ResponseEntity.ok("Préstamo realizado con éxito");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Error al realizar el préstamo");
        }
    }

    // VER PRESTAMOS
    @GetMapping("/list")
    public ResponseEntity<List<PActivo>> verPrestamos() {
        List<PActivo> prestamos = service.verPrestamos();
        return ResponseEntity.ok(prestamos);
    }

    // ACTUALIZAR
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<PActivo> actualizarPrestamo(@PathVariable Long id,
                                                      @RequestBody PActivo prestamo) {

        ResponseEntity<PActivo> response = service.actualizarP(id, prestamo);
        return response;
    }

    // VER UNO
    @GetMapping("/{id}")
    public ResponseEntity<PActivo> verPrestamo(@PathVariable Long id) {
        Optional<PActivo> prestamo = service.unPrestamo(id);
        return prestamo.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // ELIMINAR PRESTAMO
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> eliminarPrestamo(@PathVariable Long id) {
        service.eliminarP(id);
        return ResponseEntity.ok("Préstamo eliminado correctamente.");
    }
    // EXPORTAR CSV
    @GetMapping("/exportCsv")
    public void exportPrestamosCs(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"prestamos-biblioteca.csv\"");

        exportCsv.PActivoCsv(service.verPrestamos(), response.getOutputStream());
    }
    // LISTADO DE PRESTAMOS ACTIVOS POR ID USUARIO
    @GetMapping("/userList")
    public ResponseEntity<List<PActivo>> verPrestamosUsuario(@RequestParam Long id) {
        try {
            List<PActivo> prestamoUser = repo.findByUserId(id);

            // verificar si se encontraron préstamos
            if (prestamoUser == null || prestamoUser.isEmpty()) {
                // si no se encontraron préstamos, devolver un NO_CONTENT (204)
                return ResponseEntity.noContent().build();
            }

            // si se encuentra devuelve 200 ok
            return ResponseEntity.ok(prestamoUser);
        } catch (Exception e) {
            // si hay excepción devuelve un INTERNAL_SERVER_ERROR (500)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // LISTADO DE PRESTAMOS ACTIVOS POR ID LIBRO


}
