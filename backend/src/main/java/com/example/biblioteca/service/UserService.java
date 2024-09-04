package com.example.biblioteca.service;

import com.example.biblioteca.dto.UserDTO;
import com.example.biblioteca.entity.*;
import com.example.biblioteca.repository.PActivoRepo;
import com.example.biblioteca.repository.PInactivoRepo;
import com.example.biblioteca.repository.TokenRepo;
import com.example.biblioteca.repository.UserRepo;
import com.example.biblioteca.security.JwtService;
import com.example.biblioteca.service.request.Auth;
import com.example.biblioteca.service.response.ErrorResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.biblioteca.entity.Role.ADMIN;
import static com.example.biblioteca.entity.Role.USER;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    private  UserRepo userRepo;

    @Autowired
    private  PActivoRepo activoRepo;

    @Autowired
    private  TokenRepo tokenRepo;

    @Autowired
    private  TokenService tokenService;

    @Autowired
    private PInactivoRepo inactivoRepo;
    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(("El usuario no se ha encontrado")));
    }

    public List<User> findAll() {
        return userRepo.findAll().stream()
                .sorted(Comparator.comparing(User::getId)) // organiza segun id
                .collect(Collectors.toList());
    }

    public Auth guardarUser(User u) {
        // ver si existe el primer user con email
        boolean userExists = userRepo.findByEmail(u.getEmail()).isPresent();
        if (userExists) {
            throw new IllegalStateException("Email ya registrado");
        }
        User user = new User();

        // si no existe, se empiezan a crear todos
        user.setNombre(u.getNombre());
        user.setApellido(u.getApellido());
        user.setTelefono(u.getTelefono());
        user.setPassword(passwordEncoder.encode(u.getPassword()));
        user.setEmail(u.getEmail());
        user.setEstado("Activo");
        user.setEnabled(true);
        // asignar el rol
        if("ADMIN".equals(u.getRole())){
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }
        userRepo.save(user);
        // generar token para cada uno
        String generatedToken = jwtService.getToken(user);
        Token token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .confirmedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepo.save(token);

        return new Auth(generatedToken, user);
    }
    // guardar user desde admin

    public User guardarUserxAdmin(User u) {
        // ver si existe el primer user con email
        boolean userExists = userRepo.findByEmail(u.getEmail()).isPresent();
        if (userExists) {
            throw new IllegalStateException("Email ya registrado");
        }
        User user = new User();

        // si no existe, se empiezan a crear todos
        user.setNombre(u.getNombre());
        user.setApellido(u.getApellido());
        user.setTelefono(u.getTelefono());
        user.setPassword(passwordEncoder.encode(u.getPassword()));
        user.setEmail(u.getEmail());
        user.setEstado("Activo");
        user.setEnabled(true);
        // asignar el rol
    System.out.println("ROL: "+ user.getRole());
        try {
            Role role = Role.valueOf(String.valueOf(u.getRole()));
            user.setRole(role);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol invalido: " + u.getRole());
        }
        userRepo.save(user);
        // generar token para cada uno
        String generatedToken = jwtService.getToken(user);
        Token token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .confirmedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepo.save(token);

        return user;
    }
    @Transactional
    public List<User> verUsuarios() {
        return userRepo.findAll().stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    public List<User> verUserActivos(){
        return userRepo.findByEstado("Activo").stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }
    //convertir dtoa entidad usuario
    public User convertToEntity(UserDTO userDTO) {
        User user = new User();

        user.setNombre(userDTO.getNom());
        user.setApellido(userDTO.getApe());
        user.setTelefono(userDTO.getTel());
        user.setEmail(userDTO.getUseremail());
        return user;
    }

    @Transactional
    public Optional<User> updateUser(Long id, User nuevo) {
        Optional<User> existe = userRepo.findByEmail(nuevo.getEmail());

        if(existe.isPresent() && !existe.get().getId().equals(id)) {
            throw new IllegalArgumentException("El email ya existe");
        }
       return userRepo.findById(id)
                .map(user -> {
                    user.setNombre(nuevo.getNombre());
                    user.setApellido(nuevo.getApellido());
                    user.setEmail(nuevo.getEmail());

                    user.setTelefono(nuevo.getTelefono());
                    return userRepo.save(user);
                });
    }



    public User unUser(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    public List<PActivo> findActivosByUserId(Long userId) {
        return activoRepo.findByUserId(userId);
    }
    //ELIMINAR
    @Transactional
    public ResponseEntity<?> deleteUser(Long id) {
        Optional<User> optionalUser = userRepo.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // verifica si tiene prestamos activos o inactivos
            List<PActivo> prestamoActivo = activoRepo.findByUser(user);
            List<PInactivo> prestamoInactivo = inactivoRepo.findByUser(user);

            if (!prestamoActivo.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("CONFLICT", "No se puede eliminar el usuario porque tiene préstamos activos."));
            } else if (!prestamoInactivo.isEmpty()) {
                user.setEstado("Inactivo");
                userRepo.save(user);
                return ResponseEntity.ok().body(null);
            }

            // si no tiene préstamos activos ni inactivos, se puede eliminar
            try {
                tokenRepo.deleteByUser(user);
                userRepo.deleteById(id);
                return ResponseEntity.noContent().build(); // Use noContent() instead of ok()
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "Error al eliminar el usuario."));
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    /*
    public ResponseEntity<?> deleteUser(Long id) {

        Optional<User> optionalUser = userRepo.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // verifica si tiene pretamos activos
            List<PActivo> prestamoActivo = activoRepo.findByUser(user);
            if (!prestamoActivo.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("BAD_REQUEST", "No se puede eliminar el usuario porque tiene préstamos activos."));
            }

            // si no tiene entonces se puede eliminar
            try {
                tokenRepo.deleteByUser(user);
                userRepo.deleteById(id);
                return ResponseEntity.ok().body(null);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "Error al eliminar el usuario."));
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }*/

    // CAMBIAR DE ROL
    public User updateUserRole(Long id) {
        User nuevo = userRepo.findById(id).orElseThrow();
        nuevo.setRole(ADMIN);

        return userRepo.save(nuevo);
    }


}
