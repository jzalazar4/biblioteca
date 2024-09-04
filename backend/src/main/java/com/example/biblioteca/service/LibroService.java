package com.example.biblioteca.service;

import com.example.biblioteca.entity.Libro;
import com.example.biblioteca.entity.PActivo;
import com.example.biblioteca.entity.User;
import com.example.biblioteca.repository.LibroRepo;
import com.example.biblioteca.repository.PActivoRepo;
import com.example.biblioteca.repository.PInactivoRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import com.example.biblioteca.service.response.ErrorResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class LibroService {

    private final LibroRepo libroRepo;
    private final PActivoRepo activoRepo;
    private final PInactivoRepo inactivoRepo;

    public Libro newBook(Libro libro) {
        return libroRepo.save(libro);
    }
    public List<Libro> uploadCsv(List<MultipartFile> files) throws IOException {
        List<Libro> libros = new ArrayList<>();

        for (MultipartFile file : files) {

            try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
                 CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(br)) {

                // primero capta los headers y despues los pone en minuscula
                Map<String, String> headers = parser.getHeaderNames().stream()
                        .collect(Collectors.toMap(
                                header -> header.toLowerCase(),
                                header -> header
                        ));

                // leer y agregar libros
                for (CSVRecord record : parser) {
                    Libro libro = new Libro();

                    for (String key : headers.keySet()) {
                        String header = headers.get(key);
                        String value = record.get(header);

                        // puede haber valores o columnas vacias, entonces
                        // se marcan como NA
                        if (value == null || value.isEmpty()) {
                            value = "NA";
                        }

                        // switch para ver si existe el encabezado y si esta, se guardan los datos
                        switch (key) {
                            case "titulo":
                                libro.setTitulo(value);
                                break;
                            case "autor":
                                libro.setAutor(value);
                                break;
                            case "pag":
                                libro.setPag(value != null ? Integer.parseInt(value) : 0); // 0 como default
                                break;
                            case "genero":
                                libro.setGenero(value);
                                break;
                            case "rating":
                                libro.setRating(value != null ? Float.parseFloat(value) : 0f);
                                break;
                            case "isbn":
                                libro.setIsbn(value != null ? Integer.parseInt(value) : 0);
                                break;
                            case "imagen":
                                libro.setImagen(value != null ? value: "");
                                break;
                            case "fecha":
                                libro.setFecha(value != null ? Integer.parseInt(value) : 0);
                                break;
                            case "sinopsis":
                                libro.setSinopsis(value != null ? value: "");
                                break;
                                /*el estado si no hay datos esta N/A pero si hay stock deberia estar disponible
                                * entonces en stock verifico si hay datos y si hay cuantos libros hay
                                * y ahi setteo el estado del libro*/
                            case "stock":
                                int stockValue = value != null ? Integer.parseInt(value) : 0;
                                libro.setStock(stockValue);
                                if (stockValue > 0) {
                                    libro.setEstado("Disponible");
                                } else if (stockValue == 0) {
                                    libro.setEstado("No Disponible");
                                } else {
                                    libro.setEstado("NA");
                                }
                                break;


                        }
                    }

                    libros.add(libro);
                }
            }
        }

        // Guardar todos los libros en la base de datos
        libroRepo.saveAll(libros);

        return libros;
    }

    public Libro guardarLibro(Libro l) {
        return libroRepo.save(l);
    }

    public ResponseEntity<Libro> actualizarLibro(Long id, Libro nuevo){
        return libroRepo.findById(id)
                .map(libro -> {
                    libro.setTitulo(nuevo.getTitulo());
                    libro.setAutor(nuevo.getAutor());
                    libro.setFecha(nuevo.getFecha());
                    libro.setSinopsis(nuevo.getSinopsis());
                    libro.setPag(nuevo.getPag());
                    libro.setStock(nuevo.getStock());
                    libro.setGenero(nuevo.getGenero());
                    libro.setEstado(nuevo.getEstado());
                    libro.setImagen(nuevo.getImagen());
                    libro.setIsbn(nuevo.getIsbn());

                    Libro actualizado = libroRepo.save(libro);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());

    }
@Transactional
    public List<Libro> verLibros() {
        return libroRepo.findAll().stream()
                .sorted(Comparator.comparing(Libro::getId)) // ordena los libros por id
                .collect(Collectors.toList());
    }

    public Libro unLibro(Long id) {
        return libroRepo.findById(id).orElse(null);
    }

    // listado libros no archivados
    public List<Libro>  listadoLibros(){
        return libroRepo.findAllByEstadoNot("Archivado").stream()
                .sorted(Comparator.comparing(Libro::getId))
                .collect(Collectors.toList());
    }
    // ELIMINAR LIBRO, VERIFICANDO SI TIENE UN PRESTAMO ACTIVo

    public ResponseEntity<?> eliminarLibro(Long id) {
        return libroRepo.findById(id)
                .map(libro -> {
                    List<PActivo> prestamosActivos = activoRepo.findByLibroAndEstado(libro, "Activo");
                    if (!prestamosActivos.isEmpty()) {
                        // si esta activo no se borra
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(new ErrorResponse("CONFLICT", "No se puede eliminar el libro porque está siendo prestado."));
                    } else if (inactivoRepo.findByLibro(libro) != null) {
                        // si esta en prestamo inactivo se archiva asi no se pierde
                        libro.setEstado("Archivado");
                        libroRepo.save(libro);
                        return ResponseEntity.noContent().build();
                    } else {
                        // no esta en ninguna tabla
                        libroRepo.delete(libro);
                        return ResponseEntity.noContent().build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
    // FILTRAR LIBROS SEGUN AUTOR, TITULO, GENERO O RATING

    public List<Libro> findByTitulo(String titulo) {
        return libroRepo.findByTituloContainingIgnoreCase(titulo);
    }

    public List<Libro> findByGenero(String genero) {
        return libroRepo.findByGeneroContainingIgnoreCase(genero);
    }

    public List<Libro> findByAutor(String autor) {
        return libroRepo.findByAutorContainingIgnoreCase(autor);
    }

    public List<Libro> findByRating(float rating) {
        return libroRepo.findByRatingGreaterThanEqual(rating);
    }

    public List<Libro> findByStock(Long stock) {
        return libroRepo.findByStock(stock);
    }
}

