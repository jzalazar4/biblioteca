package com.example.biblioteca.controller;


import com.example.biblioteca.entity.Libro;
import com.example.biblioteca.repository.LibroRepo;
import com.example.biblioteca.repository.PActivoRepo;
import com.example.biblioteca.service.LibroService;
import com.example.biblioteca.service.csv.ExportCsv;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@AllArgsConstructor
@RestController
@Secured("permitAll")
@RequestMapping(path = "/api/v1/book")
public class LibroC {

    private final LibroRepo libroRepo;
    private final LibroService service;
    private final PActivoRepo activoRepo;

    @Autowired
    private ExportCsv exportCsv;

    @PostMapping(path="/save")
    public Libro guardarLibro(@RequestBody Libro libro) {
        return service.newBook(libro);
    }

    // Cargar csv
    @PostMapping("/upload")
    public ResponseEntity<String> uploadCsvFiles(@RequestParam("files") List<MultipartFile> files) {
        try {
            List<Libro> libros = service.uploadCsv(files);
            return ResponseEntity.ok("Libros subidos correctamente: " + libros.size());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar los archivos: " + e.getMessage());
        }
    }
    /*funciona
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            List<Libro> libros = service.uploadCsv(file);
            return ResponseEntity.ok("El CSV fue recibido y procesado con éxito.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir el CSV: " + e.getMessage());
        }
    }*/
    // exportar libro a csv
    @GetMapping("/exportCsv")
    public void exportLibroCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"libros-biblioteca.csv\"");

        exportCsv.librosCsv(service.verLibros(), response.getOutputStream());
    }


    // ACTUALIZAR LIBRO
    @PutMapping(path="/update/{id}")
    public ResponseEntity<Libro> actualizarLibro(@PathVariable Long id, @RequestBody Libro nuevo) {

        return service.actualizarLibro(id, nuevo);
    }

    @GetMapping(path="/list")// FUNCIONA
    public List<Libro> verLibros() {

        return service.verLibros();
    }
    // listado sin libros archivodos
    @GetMapping(path = "/activeList")
    public List<Libro> verLibrosActivos() {
        return service.listadoLibros();
    }

    @GetMapping(path="/{id}")   //FUNCIONA
    public Libro unLibro(@PathVariable Long id) {
        return service.unLibro(id);
    }

    @DeleteMapping(path="/delete/{id}")
    public ResponseEntity<?> eliminarLibro(@PathVariable Long id) {
        return service.eliminarLibro(id);
    }


    @GetMapping("/search")
    public List<Libro> searchBooks(@RequestParam(required = false) String titulo,
                                   @RequestParam(required = false) String genero,
                                   @RequestParam(required = false) String autor,
                                   @RequestParam(required = false) Long stock) {
        if (titulo != null) {
            return service.findByTitulo(titulo);
        } else if (genero != null) {
            return service.findByGenero(genero);
        } else if (autor != null) {
            return service.findByAutor(autor);
        } else if (stock != null) {
            return service.findByStock(stock);
        } else {
            return service.verLibros();
        }
    }

}
