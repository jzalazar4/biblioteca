package com.example.biblioteca.controller;

import com.example.biblioteca.service.StatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/stats")
public class StatC {

    @Autowired
    private StatService service;

    // usuarios regisrados
    @GetMapping("/userReg")
    public Long userReg() {
        return service.userRegistrados();
    }

    //libros registrados
    @GetMapping("/bookReg")
    public Long bookReg() {
        return service.librosRegistrados();
    }

    //prestamos activos
    @GetMapping("/activoReg")
    public Long activoReg() {
        return service.activosRegistrados();
    }

    // inactivos reg
    @GetMapping("/inactivoReg")
    public Long inactivoReg(){
        return service.inactivosRegistrados();
    }

    //totales reg
    @GetMapping("/prestamosTotal")
    public Long prestamosTotales() {
      return service.allPrestamos();
    }

    // generos mas leidos
    @GetMapping("/genero")
    public Map<String,Long> generosLeidos() {
        return service.generosMasLeidos();
    }

    //autores mas leidos
    @GetMapping("/autores")
    public Map<String, Long> autoresLeidos() {
        return service.autoresLeidos();
    }

    //duracion prestamos
    @GetMapping("/dur_prestamos")
    public Map<String,Double> duracionPrestamos() {
        return service.promedioPrestamosPorMes();
    }

    //agrupar libros por generos
    @GetMapping("/bookgenrecant")
    public Map<String,Long> genrexLibros() {
       return service.generosxLibros();
    }
    @GetMapping("/librosxMes")
    public Map<String, Long> getLibrosBorrowedPerMonth() {
        return service.librosxMes();
    }
}
