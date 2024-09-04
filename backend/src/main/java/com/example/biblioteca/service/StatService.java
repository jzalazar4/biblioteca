package com.example.biblioteca.service;

import com.example.biblioteca.entity.Libro;
import com.example.biblioteca.entity.PActivo;
import com.example.biblioteca.entity.PInactivo;
import com.example.biblioteca.repository.LibroRepo;
import com.example.biblioteca.repository.PActivoRepo;
import com.example.biblioteca.repository.PInactivoRepo;
import com.example.biblioteca.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StatService {
    @Autowired
    private LibroRepo libroRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PActivoRepo activoRepo;
    @Autowired
    private PInactivoRepo inactivoRepo;

    // cantidad de libros registrados
    public Long librosRegistrados() {
        return libroRepo.count();
    }
    // usuarios registrados
    public Long userRegistrados() {
        return userRepo.count();
    }
    // prestamos activos registrados
    public Long activosRegistrados() {
        return activoRepo.count();
    }
    // inactivos registrados
    public Long inactivosRegistrados() {
        return inactivoRepo.count();
    }

    // prestamos en general registrados
    public Long allPrestamos() {
        Long total = inactivosRegistrados() + activosRegistrados();
        return total;
    }
    // promedio de libros prestados finalizados por mes

    /* stream es para colecciones de datos, la funcion collect sirve para
    * recolectar los datos en una coleccion. con el groupby agrupo segun el mes del prestamo activo o inactivo
    * veo la cantidad de dias entre la realizacion del prestamo y la devolucion
    * NO USE */
    public Map<String, Double> promedioPrestamosPorMes() {
        return inactivoRepo.findAll().stream()
                .collect(Collectors.groupingBy(p ->
                        Month.of(p.getFinicio().getMonth()).getDisplayName(TextStyle.FULL, new Locale("es", "ES")),
                        Collectors.mapping(p -> ChronoUnit.DAYS.between(p.getFinicio().toInstant(), p.getFfin().toInstant()),
                                Collectors.averagingDouble(d -> d))));
    }

    //generos mas leidos
    // polar chart?
    public Map<String, Long> generosMasLeidos() {
        // en un mapa (como diccionario) se insertan los datos de los prestamos primero como un stream
        // para usar las funciones y agrupa segun los genero y lo devuelve.
        // deberia devolver Suspenso: 4, Ciencia Ficción: 1, etc
        Map<String, Long> generosMasLeidosActive = activoRepo.findAll().stream()
                .collect(Collectors.groupingBy(p -> p.getLibro().getGenero(), Collectors.counting()));
        Map<String, Long> generosMasLeidosInactive = inactivoRepo.findAll().stream()
                .collect(Collectors.groupingBy(p -> p.getLibro().getGenero(), Collectors.counting()));

        // combina los dos mapas
        Map<String, Long> generosMasLeidos = new HashMap<>(generosMasLeidosActive);
        generosMasLeidosInactive.forEach((key, value) ->
                generosMasLeidos.merge(key, value, Long::sum));

        return generosMasLeidos;
    }

    // agrupar generos por cantidad de libros
    // barras horizontales
    public Map<String, Long> generosxLibros() {
        // listar todos los prestamos activos e inactivos
        List<PActivo> activos = activoRepo.findAll();
        List<PInactivo> inactivos = inactivoRepo.findAll();

        // combina ambos en un stream
        Map<String, Long> generosxlibros = Stream.concat(
                        activos.stream().map(activo -> activo.getLibro()),
                        inactivos.stream().map(inactivo -> inactivo.getLibro())
                )
                .distinct() // elimina los duplicados
                // agrupa los libros segun el genero y los va contando
                .collect(Collectors.groupingBy(Libro::getGenero, Collectors.counting()));

        return generosxlibros;
    }
    // top 5 autores mas leidos
    // grafico de torta
    public Map<String, Long> autoresLeidos() {

        /* combina los préstamos de autores activos e inactivos,
            agrupa segun autores y los cuenta.
            Map sirve como diccionario donde cada elemento tiene su valor
        */
        Map<String, Long> autoresActivo = activoRepo.findAll().stream()
                .collect(Collectors.groupingBy(p -> p.getLibro().getAutor(), Collectors.counting()));

        Map<String, Long> autoresInactivo = inactivoRepo.findAll().stream()
                .collect(Collectors.groupingBy(p -> p.getLibro().getAutor(), Collectors.counting()));

        // combina ambos map y con forEach y merge, va asignando a cada autor cuantas veces se
        // repitio su nombre y cuenta el total
        Map<String, Long> autoresMasLeidos = new HashMap<>(autoresActivo);
        autoresInactivo.forEach((autor, count) ->
                autoresMasLeidos.merge(autor, count, Long::sum));

        // ordena los autores por la cantidad de préstamos
        List<Map.Entry<String, Long>> sortedAuthors = autoresMasLeidos.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

        // toma los top 5 autores

        Map<String, Long> top5Autores = sortedAuthors.stream()
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return top5Autores;
    }


    // cantidad de libros prestados por mes
    // grafico de barras
    public Map<String, Long> librosxMes() {
        // solo interesa saber el mes de los prestamos
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");

        // primero hago el dato como objeto instan y despues zoneid ue usa la hora del sistema actual
        Map<String, Long> activosxMes = activoRepo.findAll().stream()
                .collect(Collectors.groupingBy(p -> formatter.format(LocalDate.from(p.getFinicio().toInstant().atZone(ZoneId.systemDefault()))), Collectors.counting()));

        Map<String, Long> inactivosxMes = inactivoRepo.findAll().stream()
                .collect(Collectors.groupingBy(p -> formatter.format(LocalDate.from(p.getFinicio().toInstant().atZone(ZoneId.systemDefault()))), Collectors.counting()));

        // combina los dos mapas de activos e inactivos
        Map<String, Long> librosTotales = new HashMap<>(activosxMes);
        inactivosxMes.forEach((key, value) -> librosTotales.merge(key, value, Long::sum));

        return librosTotales;
    }
}
