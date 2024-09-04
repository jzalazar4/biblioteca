package com.example.biblioteca.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class LibroDTO {

    @Getter @Setter
    String titulo;

    @Getter @Setter
    String autor;

    @Getter @Setter
    String genero;

    @Getter @Setter
    int pag;

    @Getter @Setter
    float rating;

    @Getter @Setter
    String sinopsis;

    @Getter @Setter
    String estado;

    @Getter @Setter
    int stock;

    @Getter @Setter
    String imagen;

    @Getter @Setter
    int fecha;

    @Getter @Setter
    int isbn;

}
