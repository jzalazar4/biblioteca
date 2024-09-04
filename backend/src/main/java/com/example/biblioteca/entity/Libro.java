package com.example.biblioteca.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
@AllArgsConstructor
@Data
@Entity
@Table(name = "libros")
public class Libro implements Serializable {

    @Id
    @Column(name = "ID_LIBRO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Getter @Setter
    @Column(name = "TITULO")
    private String titulo;

    @Getter @Setter
    @Column(name = "AUTOR")
    private String autor;

    @Getter @Setter
    @Column(name = "PAGINAS")
    private int pag;

    @Getter @Setter
    @Column(name = "GENERO")
    private String genero;

    @Getter @Setter
    @Column(name = "RATING")
    private double rating;

    @Getter @Setter
    @Lob
    @Column(name = "SINOPSIS")
    private String sinopsis;

    @Getter @Setter
    @Column(name = "ESTADO")
    private String estado;

    @Getter @Setter
    @Column(name = "STOCK")
    private int stock;

    @Getter @Setter
    @Column(name = "ISBN")
    private int isbn;

    @Getter @Setter
    @Column(name = "IMAGEN")
    private String imagen;

    @Getter @Setter
    @Column(name = "F.PUBLICACION")
    private int fecha;

    public Libro(){

    }


    public void prestar() {
        if (stock > 0) {
            stock--;
        } else {
            throw new IllegalStateException("No hay stock disponible para el libro: " + titulo);
        }
    }

    public void devolver() {
        stock++;
    }
}
