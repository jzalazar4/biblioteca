package com.example.biblioteca.service;

import com.example.biblioteca.entity.*;
import com.example.biblioteca.repository.*;
import com.example.biblioteca.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.example.biblioteca.service.response.ErrorResponse;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PAService {

    @Autowired
    public TokenRepo tokenRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    public LibroRepo libroRepo;
    @Autowired
    public PActivoRepo repo;
    @Autowired
    public UserRepo userRepo;
    @Autowired
    public PInactivoRepo inactivoRepo;


    @Transactional
    public void initializeUserPrestamos() {
        if(userRepo.findByEmail("user1@example.com").isPresent()) { // si existe, ya esatn registrados
            System.out.println("Usuarios y préstamos creados");

        } else {
            // si no, se generan
            if (userRepo.findByEmail("user1@example.com").isPresent()) {
                System.out.println("Usuarios y préstamos creados");
            } else {
                // CREAR LIBROS
                List<String> titulos = Arrays.asList(
                        "Brave New World", "The Hedge Knight", "After the Funeral",
                        "The Complete Poems: Anne Sexton", "There's a Boy in the Girls' Bathroom", "Crash Into You",
                        "Cuentos de Eva Luna", "Save the Date", "Oracle Night",
                        "Time and Again", "The Life and Adventures of Martin Chuzzlewit", "The Green Mile",
                        "Black Holes and Baby Universes and Other Essays", "I'll Be Seeing You", "Blockade Billy",
                        "Twenty Wishes", "La Isla del Día de Antes", "To Build a Fire",
                        "Good Wives", "The Darkest Hour"
                );

                // AUTORES
                List<String> autores = Arrays.asList(
                        "Aldous Huxley", "George R.R. Martin", "Agatha Christie",
                        "Anne Sexton", "Louis Sachar", "Katie McGarry",
                        "Isabel Allende", "Mary Kay Andrews", "Paul Auster",
                        "Jack Finney", "Charles Dickens", "Stephen King",
                        "Stephen Hawkings", "Mary Higgins Clark", "Stephen King",
                        "Debbie Macomber", "Umberto Eco", "Jack London",
                        "Louisa May Alcott", "Maya Banks"
                );
                // PAGINAS
                List<Integer> paginas = Arrays.asList(
                        283, 280, 195, 230, 360, 373, 412, 473, 305, 474, 304,
                        390, 475, 182, 185, 362, 431, 345, 171, 430
                );

                //ISBN
                List<Integer> isbn = Arrays.asList(
                        16335101, 40847773, 1812530, 46720, 639745,
                        23751121, 130037, 26037952, 3011932,
                        6887879, 901325, 9813490, 64240, 2643785,
                        11366182, 4084010, 1792697, 1618080, 6582437, 6828809
                );
                // FECHA

                List<Integer> anio = Arrays.asList(
                        1958, 1998, 1953, 1981, 1987, 2013, 1988, 2014,
                        2004, 1970, 1844, 1996, 1993, 1993, 2010, 2008,
                        1994, 1903, 1868, 2010
                );
                // RATING
                List<Double> ratings = Arrays.asList(
                        3.93, 4.23, 3.84, 4.28, 3.95, 4.28, 3.96,
                        3.87, 3.77, 3.98, 3.82, 4.52, 4.09, 3.79,
                        3.4, 4.16, 3.43, 3.92, 3.92, 4.17
                );
                // GENEROS
                List<String> generos = Arrays.asList(
                        "Ciencia Ficción", "Fantasía", "Suspenso", "Poesía",
                        "Juvenil", "Romance", "Ficción", "Romance", "Suspenso",
                        "Ciencia Ficción", "Clásico", "Suspenso", "Ensayo",
                        "Suspenso", "Suspenso", "Romance", "Novela", "Cuento",
                        "Clásico", "Romance"
                );
                // CREAR LIBROS
                /* por cada libro del array, se va creando un total de 20 libros*/
                List<Libro> libros = IntStream.range(0, 20)
                        .mapToObj(i -> {
                            Libro libro = new Libro();
                            libro.setTitulo(titulos.get(i));
                            libro.setAutor(autores.get(i));
                            libro.setStock(2);
                            libro.setEstado("Disponible");
                            libro.setFecha(anio.get(i));
                            libro.setGenero(generos.get(i));
                            libro.setRating(ratings.get(i));
                            libro.setIsbn(isbn.get(i));
                            libro.setImagen(null);
                            libro.setPag(paginas.get(i));
                            return libro;
                        })
                        .collect(Collectors.toList()); // colecciona todos los libros
                libroRepo.saveAll(libros);

                // USUARIOS
                List<User> usuarios = IntStream.range(0, 20)
                        .mapToObj(i -> {
                            User user = new User();
                            user.setEmail("user" + (i + 1) + "@example.com");
                            user.setPassword(passwordEncoder.encode("1234"));
                            user.setNombre("User " + (i + 1));
                            user.setApellido("Apellido " + (i + 1));
                            user.setEstado("Activo");
                            user.setTelefono(Long.valueOf(123456789 + i));
                            user.setRole(Role.USER);
                            user.setEnabled(true);
                            user.setLocked(false);
                            return user;
                        })
                        .collect(Collectors.toList());

                userRepo.saveAll(usuarios);
                usuarios.forEach(user -> {
                    String generatedToken = jwtService.getToken(user);
                    Token token = Token.builder()
                            .token(generatedToken)
                            .createdAt(LocalDateTime.now())
                            .expiresAt(LocalDateTime.now().plusMinutes(15))
                            .confirmedAt(LocalDateTime.now())
                            .user(user)
                            .build();
                    tokenRepo.save(token);
                });

                // Crear préstamos
                /* con random, selecciona un libro de forma aleatoria para cada usuario.
                 en cada vuelta del for se crea 1 prestamo activo y uno inactivo
                 y se le asigna, usuario, fecha antes de agosto (al azar igual)
                */
                Random random = new Random();
                for (int i = 0; i < 10; i++) {
                    // PRESTAMOS ACTIVOS
                    PActivo pa = new PActivo();
                    pa.setLibro(libros.get(random.nextInt(libros.size()))); // seleccion aleatoria de libro
                    pa.setUser(usuarios.get(i));
                    pa.setFinicio(randomFechaInicio());
                    pa.setEstado("Activo");
                    repo.save(pa);

                    // PRESTAMOS INACTIVOS
                    PInactivo pi = new PInactivo();
                    pi.setLibro(libros.get(random.nextInt(libros.size())));
                    pi.setUser(usuarios.get(i + 10));
                    pi.setEstado("Inactivo");
                    pi.setFinicio(randomFechaInicio());
                    // la fecha de devolucion tiene como limite la fecha del prestamo y una aleatoria
                    Date returnDate = new Date(pi.getFinicio().getTime() + (long) (Math.random() * 30 * 24 * 60 * 60 * 1000));
                    pi.setFfin(returnDate);

                    inactivoRepo.save(pi);
                }
            }
        }
    }

    private Date randomFechaInicio() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        long inicio = Timestamp.valueOf(year + "-01-01 00:00:00").getTime();
        long fin = Timestamp.valueOf(year + "-07-31 23:59:59").getTime();
        long diff = fin - inicio + 1;
        return new Date(inicio + (long) (Math.random() * diff));
    }


    // DEVOLVER PRESTAMO
    /*primero ve si existe el prestamo, si existe, obtiene los datos del prestamo
    * y reinicia los valores: estado, stock.
    * despues lo envia a prestamos inactivos*/
    @Transactional
    public ResponseEntity<String> devolverLibro(Long id) {
        Optional<PActivo> optionalPrestamo = repo.findById(id);

        if (optionalPrestamo.isPresent()) {
            PActivo prestamoActivo = optionalPrestamo.get();
            Libro libro = prestamoActivo.getLibro();
            User user = prestamoActivo.getUser();

            if (libro == null || user == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Datos de libro o usuario no encontrados.");
            }

            // Actualizar estado del préstamo y stock del libro
            prestamoActivo.setEstado("Devuelto");
            libro.setStock(libro.getStock() + 1);

            if (libro.getStock() > 0) {
                libro.setEstado("Disponible");
            }

            // Crear y guardar el préstamo inactivo
            PInactivo pi = new PInactivo();
            pi.setLibro(libro);
            pi.setUser(user);
            pi.setFinicio(prestamoActivo.getFinicio());
            pi.setFfin(new Date());
            pi.setEstado("Inactivo");
            inactivoRepo.save(pi);

            // Actualizar la lista de préstamos activos del usuario
            if (user.getPrestamos_activos() != null) {
                user.getPrestamos_activos().remove(prestamoActivo);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: la lista de préstamos activos es nula.");
            }

            // Guardar cambios en el libro y eliminar el préstamo activo
            libroRepo.save(libro);
            repo.delete(prestamoActivo);

            return ResponseEntity.ok("Libro devuelto correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Préstamo no encontrado.");
        }
    }


    // GUARDAR PRESTAMO
    // /* primero busca si existen el libro y user enviado
    //        * despues comprueba si no estan nulos, el stock y si hay un prestamo activo del mismo libro
    //        * para el mismo usuario.
    //        * si no existe un prestamo activo con ese user y libro, continua creando el prestamo*/

    @Transactional
    public ResponseEntity<ErrorResponse> nuevoPrestamo(PActivo prestamo) {
        try {
            Libro libro = libroRepo.findById(prestamo.getLibro().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado."));
            User user = userRepo.findById(prestamo.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

            verificarStock(libro);
            verificarPrestamosActivos(user, libro);

            prestamo.setEstado("Activo");

            // Disminuye el stock
            libro.setStock(libro.getStock() - 1);

            // Cambia el estado si el stock es 0
            if (libro.getStock() == 0) {
                libro.setEstado("No disponible");
            }

            // Ajuste de la fecha debido a la diferencia al guardarse en la base de datos
            if (prestamo.getFinicio() != null) {
                LocalDate fecha = LocalDate.from(prestamo.getFinicio().toInstant().atZone(ZoneId.systemDefault()));
                fecha = fecha.plusDays(1); // agrega un día a la fecha
                prestamo.setFinicio(Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }

            libroRepo.save(libro);
            repo.save(prestamo);

            return ResponseEntity.ok().body(null);

        } catch (IllegalStateException e) {
            ErrorResponse errorResponse;

            switch (e.getMessage()) {
                case "El libro no está disponible para préstamo.":
                    errorResponse = new ErrorResponse("LIBRO_NO_DISPONIBLE", e.getMessage());
                    break;
                case "El usuario ya tiene cinco libros prestados activos.":
                    errorResponse = new ErrorResponse("MAX_PRESTAMOS", e.getMessage());
                    break;
                case "El libro está actualmente prestado al usuario.":
                    errorResponse = new ErrorResponse("LIBRO_YA_PRESTADO", e.getMessage());
                    break;
                case "Usuario no encontrado.":
                    errorResponse = new ErrorResponse("USER_NOT_FOUND", e.getMessage());
                    break;
                default:
                    errorResponse = new ErrorResponse("INTERNAL_ERROR", "Error interno.");
                    break;
            }
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    /* @Transactional
    public ResponseEntity<ErrorResponse> nuevoPrestamo(PActivo prestamo) {
        try {
            Libro libro = libroRepo.findById(prestamo.getLibro().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado."));
            User user = userRepo.findById(prestamo.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

            verificarStock(libro);
            verificarPrestamosActivos(user, libro);

            prestamo.setEstado("Activo");

            // Disminuye el stock
            libro.setStock(libro.getStock() - 1);

            // Cambia el estado si el stock es 0
            if (libro.getStock() == 0) {
                libro.setEstado("No disponible");
            }
            // la fecha la recibo bien pero se ve 1 dia menos en la bbdd
            if (prestamo.getFinicio() != null) {
                LocalDate fecha = LocalDate.from(prestamo.getFinicio().toInstant().atZone(ZoneId.systemDefault()));
                fecha = fecha.plusDays(1); // agrega un día a la fecha
                prestamo.setFinicio(Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            libroRepo.save(libro);
            repo.save(prestamo);

            return ResponseEntity.ok().body(null);


        } catch (IllegalStateException e) {
            ErrorResponse errorResponse;

            if (e.getMessage().equals("El libro no está disponible para préstamo.")) {
                errorResponse = new ErrorResponse("LIBRO_NO_DISPONIBLE", e.getMessage());

            } else if (e.getMessage().equals("El usuario ya tiene cinco libros prestados activos.")) {
                errorResponse = new ErrorResponse("MAX_PRESTAMOS", e.getMessage());

            } else if (e.getMessage().equals("El libro está actualmente prestado al usuario.")) {
                errorResponse = new ErrorResponse("LIBRO_YA_PRESTADO", e.getMessage());

            } else if (e.getMessage().equals("Usuario no encontrado.")) {
                errorResponse = new ErrorResponse("USER_NOT_FOUND", e.getMessage());
            } else {
                errorResponse = new ErrorResponse("INTERNAL_ERROR", "Error interno.");
            }
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
*/
    /*@Transactional
    public ResponseEntity<PActivo> nuevoPrestamo(PActivo prestamo) {
        Libro libro = libroRepo.getById(prestamo.getLibro().getId());
        User user = userRepo.getById(prestamo.getUser().getId());

        System.out.print("titulo: " + libro.getTitulo());
        System.out.print("user: " + user.getNombre());

        try {
            verificarStock(libro);
            verificarPrestamosActivos(user, libro);

            prestamo.setEstado("Activo");

            // disminuye el stock
            libro.setStock(libro.getStock() - 1);

            // cambia el estado si el stock es 0
            if (libro.getStock() == 0) {
                libro.setEstado("No disponible");
            }

            libroRepo.save(libro);
            repo.save(prestamo);
            return ResponseEntity.ok();
        } catch (IllegalStateException e) {
            ErrorResponse errorResponse = new ErrorResponse("LIBRO_NO_DISPONIBLE", "El libro no está disponible para préstamo.");
            return ResponseEntity.badRequest().body(errorResponse);

        }catch (IllegalStateException e) {
            ErrorResponse errorResponse = new ErrorResponse("USER_NOT_FOUND", "Usuario no encontrado.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }*/
// para verificar el stock primero se ve si no esta nulo, si esta en 0, o esta no disponible
    private void verificarStock(Libro libro) {
        if (libro == null || libro.getEstado() == null || libro.getStock() == 0
                || !libro.getEstado().equalsIgnoreCase("Disponible")) {
            throw new IllegalStateException("El libro no está disponible para préstamo.");
        }
    }

    private void verificarPrestamosActivos(User user, Libro libro) {
        if (user == null) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }

        // verifica la cantidad de prestmaos activos
        List<PActivo> prestamosActivos = repo.findByUserAndEstado(user, "Activo");
        if (prestamosActivos.size() >= 5) {
            throw new IllegalStateException("El usuario ya tiene cinco libros prestados activos.");
        }

        // verifica si ya existe un prestamo activo para el mismo libro y usuario
        PActivo prestamoActivo = repo.findByLibroAndUserAndEstado(libro, user, "Activo");
        if (prestamoActivo != null) {
            throw new IllegalStateException("El libro está actualmente prestado al usuario.");
        }
    }

    public List<PActivo> verPrestamos() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(PActivo::getId))
                .collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity<PActivo> actualizarP(Long id, PActivo p) {
        System.out.println("DATOS PRESTAMO: " + p);
        Optional<PActivo> prestamoOptional = repo.findById(id);

        if (prestamoOptional.isPresent()) {
            PActivo prestamoExistente = prestamoOptional.get();

            // actualizar el libro si se genera un nuevo id de libro
            if (p.getLibro().getId() != null) {
                prestamoExistente.setLibro(p.getLibro());
            }

            // actualiza el usuario si se genera u nuevo id
            if (p.getUser().getId() != null) {
                prestamoExistente.setUser(p.getUser());
            }
            // la fecha la recibo bien pero se ve 1 dia menos en la bbdd
            if (p.getFinicio() != null) {
                LocalDate fecha = LocalDate.from(p.getFinicio().toInstant().atZone(ZoneId.systemDefault()));
                fecha = fecha.plusDays(1); // agrega un día a la fecha
                prestamoExistente.setFinicio(Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }

            PActivo prestamoActualizado = repo.save(prestamoExistente);
            return ResponseEntity.ok(prestamoActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public Optional<PActivo> unPrestamo(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public void eliminarP(Long id) {
        Optional<PActivo> optionalPrestamo = repo.findById(id);
        PActivo prestamo = optionalPrestamo.orElseThrow(() ->
                new RuntimeException("No se pudo encontrar el préstamo"));

        Long idLibro = prestamo.getLibro().getId();
        Optional<Libro> optionalLibro = libroRepo.findById(idLibro);
        Libro libro = optionalLibro.orElseThrow(() ->
                new RuntimeException("No se pudo encontrar el libro"));

        // aumenta el stock del libro al eliminar el préstamo
        libro.setStock(libro.getStock() + 1);

        // cambia el estado del libro a "disponible" si el stock es mayor a 0
        if (libro.getStock() > 0) {
            libro.setEstado("Disponible");
        }

        libroRepo.save(libro);
        repo.deleteById(id);
    }

}
