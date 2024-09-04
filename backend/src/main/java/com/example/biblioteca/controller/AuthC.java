package com.example.biblioteca.controller;

import com.example.biblioteca.dto.NewPasswordDTO;
import com.example.biblioteca.dto.PasswordResetDTO;
import com.example.biblioteca.dto.UserDTO;
import com.example.biblioteca.entity.Token;
import com.example.biblioteca.entity.User;
import com.example.biblioteca.repository.TokenRepo;
import com.example.biblioteca.repository.UserRepo;
import com.example.biblioteca.security.JwtService;
import com.example.biblioteca.service.*;
import com.example.biblioteca.service.request.Auth;
import com.example.biblioteca.service.request.AuthRequest;
import com.example.biblioteca.service.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/")
public class AuthC {

    private final AuthService service;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final TokenRepo tokenRepo;

    @GetMapping(path = "/userlist")
    public List<User> verUsuarios() {
        return userRepo.findAll();
    }
//arreglar esto
@PostMapping("/refresh-token")
public ResponseEntity<?> refreshToken(@RequestParam("token") String tokenRefresh) {
    try {
        Optional<Token> t = tokenService.findByToken(tokenRefresh);

        if (t.isPresent()) {
            Token token = t.get();
            LocalDateTime expiresAt = token.getExpiresAt();
            LocalDateTime currentTime = LocalDateTime.now();

            // Verificar si el token ha expirado o si quedan 5 minutos para que expire
            if (expiresAt.isBefore(currentTime) || expiresAt.isBefore(currentTime.plusMinutes(5))) {
                // Token expirado o a punto de expirar en 5 minutos
                User user = token.getUser();

                // Generar nuevo token
                String nuevoToken = jwtService.getRefreshToken(user);

                // Actualizar la información del token en la base de datos
                token.setToken(nuevoToken);
                token.setExpiresAt(currentTime.plusMinutes(60)); // caduca en 1 hora
                tokenRepo.save(token);

                // Enviar el nuevo token y la fecha de expiración al frontend
                Map<String, String> response = new HashMap<>();
                response.put("token", nuevoToken);
                response.put("expiresAt", token.getExpiresAt().toString());

                return ResponseEntity.ok(response);
            } else {
                // Token sigue siendo válido y no requiere renovación
                return ResponseEntity.ok("Token válido");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no encontrado");
        }
    } catch (ExpiredJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT ha expirado");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error al refrescar el token");
    }
}

    /*
@PostMapping("/refresh-token")

public ResponseEntity<?> refreshToken(@RequestParam("token") String tokenRefresh) {
    try {
        Optional<Token> t = tokenService.findByToken(tokenRefresh);

        if (t.isPresent()) {
            Token token = t.get();
            LocalDateTime expiresAt = token.getExpiresAt();
            LocalDateTime currentTime = LocalDateTime.now();

            // Verificar si el token ha expirado o si quedan 5 minutos para que expire
            if (expiresAt.isBefore(currentTime) || expiresAt.isBefore(currentTime.plusMinutes(5))) {
                // Token expirado o a punto de expirar en 5 minutos
                User user = token.getUser();
                String nuevoToken = jwtService.getRefreshToken(user);
                token.setToken(nuevoToken);
                token.setExpiresAt(LocalDateTime.now().plusMinutes(60)); // caduca en 1 hora
                tokenRepo.save(token);

                // Enviar el nuevo token y la fecha de expiración al frontend
                Map<String, String> response = new HashMap<>();
                response.put("token", nuevoToken);
                response.put("expiresAt", token.getExpiresAt().toString());

                return ResponseEntity.ok(response);
            } else {
                // Token sigue siendo válido y no requiere renovación
                return ResponseEntity.ok("Token válido");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no encontrado");
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error al refrescar el token");
    }
}
*/


    // @Transactional
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            String token = service.register(userDTO);
            return ResponseEntity.ok("Solicitud enviada. Confirme su cuenta usando el enlace enviado a su email.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el registro");
        }
    }

    // Registrar user por admin ya habilitado
    @PostMapping("/registerUser")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
          service.registerUser(userDTO);
            return ResponseEntity.ok("Usuario agregado.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el registro");
        }
    }

    @SneakyThrows
    @Transactional
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        try {
            Auth response = service.authenticate(request);
            String json = objectMapper.writeValueAsString(response);
            return ResponseEntity.ok(json);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ExceptionHandler({ BadCredentialsException.class, DisabledException.class, AuthenticationException.class })
    public ResponseEntity<String> handleAuthenticationException(Exception e) {
        if (e instanceof BadCredentialsException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email o contraseña incorrectos");
        } else if (e instanceof DisabledException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cuenta no verificada. Revise su email");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Autenticación fallida");
        }
    }
    @Transactional
    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        try {
            String result = service.confirm(token);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la confirmación");
        }
    }

    @PostMapping(path = "/resendEmail")
    public ResponseEntity<?> resendEmail(@RequestParam("email") String email) {
        try {
            service.resendEmail(email);
            return ResponseEntity.ok("Email enviado");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar el email");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetDTO passwordResetDTO) {
        try {
            service.forgotPasswordToken(passwordResetDTO.getEmail());
            return ResponseEntity.ok("Email para reestablecer la contraseña enviado");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar el email para reestablecer la contraseña");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                           @RequestBody NewPasswordDTO newPasswordDTO) {
        try {
            service.resetPassword(token, newPasswordDTO.getNewpassword());
            return ResponseEntity.ok("Contraseña restablecida con éxito");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al restablecer la contraseña");
        }
    }


}
