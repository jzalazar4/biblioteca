package com.example.biblioteca.service.csv;

import com.example.biblioteca.entity.Libro;
import com.example.biblioteca.entity.PActivo;
import com.example.biblioteca.entity.PInactivo;
import com.example.biblioteca.entity.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

@Component
public class ExportCsv {

    // exportar libros
    public boolean librosCsv(List<Libro> libros, OutputStream outputStream) {
        try {
            try (Writer writer = new OutputStreamWriter(outputStream)) {
                CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);

                // headers
                printer.printRecord("ID", "Título", "Autor", "Género", "Páginas", "Fecha",
                        "ISBN", "Sinopsis", "Stock", "Rating", "Imagen", "Estado");

                for (Libro libro: libros) {
                    printer.printRecord(libro.getId(),
                            libro.getTitulo(),
                            libro.getAutor(),
                            libro.getGenero(),
                            libro.getPag(),
                            libro.getFecha(),
                            libro.getIsbn(),
                            libro.getSinopsis(),
                            libro.getStock(),
                            libro.getRating(),
                            libro.getImagen(),
                            libro.getEstado()

                    );
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    // exportar usuarios
    public boolean userCsv(List<User> usuarios, OutputStream outputStream) {
        try {
            try (Writer writer = new OutputStreamWriter(outputStream)) {
                CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);

                printer.printRecord("ID", "Nombre", "Apellido", "Email", "Teléfono", "Rol");

                for (User user: usuarios) {
                    printer.printRecord(user.getId(),
                            user.getNombre(),
                            user.getApellido(),
                            user.getEmail(),
                            user.getTelefono(),
                            user.getRole()
                    );
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    // exportar prestamos activos
    public boolean PActivoCsv(List<PActivo> prestamos, OutputStream outputStream) {
        try {
            try (Writer writer = new OutputStreamWriter(outputStream)) {
                CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);

                printer.printRecord("ID", "ID_Usuario","Nombre", "Apellido", "Email", "Fecha inicio","Estado",
                        "ID_Libro", "Título","Autor","Stock");

                for (PActivo activo: prestamos) {
                    printer.printRecord(
                            activo.getId(),
                            activo.getUser().getId(),
                            activo.getUser().getNombre(),
                            activo.getUser().getApellido(),
                            activo.getUser().getEmail(),
                            activo.getFinicio(),
                            activo.getEstado(),
                            activo.getLibro().getId(),
                            activo.getLibro().getTitulo(),
                            activo.getLibro().getAutor(),
                            activo.getLibro().getStock()
                    );
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    // exportar prestamos inactivos
    public boolean PInactivoCsv(List<PInactivo> prestamos, OutputStream outputStream) {
        try {
            try (Writer writer = new OutputStreamWriter(outputStream)) {
                CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);

                printer.printRecord("ID", "Fecha_inicio","Fecha_fin", "ID_User", "Nombre", "Apellido","Email",
                        "ID_Libro", "Título","Autor","Stock","Estado");

                for (PInactivo inactivo: prestamos) {
                    printer.printRecord(
                            inactivo.getId(),
                            inactivo.getFinicio(),
                            inactivo.getFfin(),
                            inactivo.getUser().getId(),
                            inactivo.getUser().getNombre(),
                            inactivo.getUser().getApellido(),
                            inactivo.getUser().getEmail(),
                            inactivo.getLibro().getId(),
                            inactivo.getLibro().getTitulo(),
                            inactivo.getLibro().getAutor(),
                            inactivo.getLibro().getStock(),
                            inactivo.getEstado()
                    );
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
