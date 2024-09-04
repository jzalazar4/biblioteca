package com.example.biblioteca.service;

import com.example.biblioteca.dto.UserDTO;
import com.example.biblioteca.entity.Role;
import com.example.biblioteca.entity.Token;
import com.example.biblioteca.entity.User;
import com.example.biblioteca.repository.TokenRepo;
import com.example.biblioteca.repository.UserRepo;
import com.example.biblioteca.security.JwtService;
import com.example.biblioteca.service.email.EmailService;

import com.example.biblioteca.service.request.Auth;
import com.example.biblioteca.service.request.AuthRequest;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class AuthService {
    private static final String CONFIRMATION_URL = "http://localhost:8082/api/v1/confirm?token=%s";
    private static final long EXPIRATION_TIME = 120000;

    @Autowired
    private UserRepo userRepository;
    @Autowired
    private TokenRepo tokenRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private  EmailService emailService;
   // private static final String resetUrl = "http://localhost:8080/reset-password?token=%s";

   @Transactional
    public void initializeAdminUser() {

       // buscar el email, si existe, ya esta inicializado
       if(userRepository.findByEmail("admin@example.com").isPresent()){
           System.out.println("ya hay admin");
       }else{
           // si no existe, se crea
            User admin = new User();
           admin.setEmail("admin@example.com");
           admin.setPassword(passwordEncoder.encode("password"));
           admin.setNombre("Admin");
           admin.setApellido("User");
           admin.setEstado("Activo");
           admin.setTelefono(Long.valueOf(123456));
           admin.setRole(Role.ADMIN);
           admin.setEnabled(true);
           admin.setLocked(false);

           userRepository.save(admin);

           // generar token
           String generatedToken = jwtService.getToken(admin);
           // String generatedToken = UUID.randomUUID().toString();
           Token token = Token.builder()
                   .token(generatedToken)
                   .createdAt(LocalDateTime.now())
                   .expiresAt(LocalDateTime.now().plusMinutes(15))
                   .confirmedAt(LocalDateTime.now())
                   .user(admin)
                   .build();


           tokenRepository.save(token);
           System.out.println("Token generado: " + token);
       }

    }
// usuarios inicializados
    @Transactional
    public String registerUser(UserDTO userDTO) {
        // ver si existe el primer user con email
        boolean userExists = userRepository.findByEmail(userDTO.getUseremail()).isPresent();
        if (userExists) {
            throw new IllegalStateException("Email ya registrado");
        }
        User user = new User();

        // si no existe, se empiezan a crear todos
        user.setNombre(userDTO.getNom());
        user.setApellido(userDTO.getApe());
        user.setTelefono(userDTO.getTel());
        user.setEstado("Activo");
        user.setPassword(passwordEncoder.encode(userDTO.getUserpassword()));
        user.setEmail(userDTO.getUseremail());

        // asignar el rol
        if("ADMIN".equals(userDTO.getRole())){
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }

        user.setEnabled(true);

        userRepository.save(user);

        // generar token para cada uno
        String generatedToken = jwtService.getToken(user);
        Token token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepository.save(token);
        System.out.print("Token creado: "+ token);

        return generatedToken;
    }

    @Transactional
    public String register(UserDTO userDTO) {
        // ver si existe el user con email
        boolean userExists = userRepository.findByEmail(userDTO.getUseremail()).isPresent();
        if (userExists) {
            throw new IllegalStateException("Email ya registrado");
        }
        User user = new User();

        user.setNombre(userDTO.getNom());
        user.setApellido(userDTO.getApe());
        user.setTelefono(userDTO.getTel());
        user.setPassword(passwordEncoder.encode(userDTO.getUserpassword()));
        user.setEmail(userDTO.getUseremail());
        user.setRole(Role.USER);
        user.setEstado("Activo");

        userRepository.save(user);


        String generatedToken = jwtService.getToken(user);
        Token token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepository.save(token);
        System.out.print("Token creado: "+ token);
        // enviar email de confirmacion
        try {
            emailService.sendEmail(
                    user.getNombre(),
                    user.getApellido(),
                    user.getEmail(),
                    "confirm-email",
                    String.format(CONFIRMATION_URL, generatedToken)
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return generatedToken;
    }
    /*
    public Auth authenticate(AuthRequest request) {
        System.out.print(request);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();

            String token = jwtService.getToken(user);
            System.out.println("Token generado: " + token);

            // verificar si existe un token segun el usuario
            Optional<Token> existingTokenOptional = tokenRepository.findByUser(user);
            System.out.println("Token encontrado: "+ existingTokenOptional.toString());
            Token tokenEntity;
            if (existingTokenOptional.isPresent()) {
                // si existe, se reemplazan los datos existentes por nuevos
                tokenEntity = existingTokenOptional.get();
                tokenEntity.setToken(token);
                tokenEntity.setCreatedAt(LocalDateTime.now());
                tokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(15));
            } else {
                // si no hay, se genera uno
                tokenEntity = Token.builder()
                        .token(token)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusMinutes(15))
                        .user(user)
                        .build();
            }


            tokenRepository.save(tokenEntity);

            return new Auth(token, user);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Email o contraseña inválido");
        } catch (DisabledException e) {
            throw new DisabledException("Cuenta no verificada");
        } catch (AuthenticationException e) {
            // Log the original exception
            System.err.println("La autenticación falló: " + e.getMessage());
            try {
                throw new Exception("La autenticación falló" + e.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

*/
    public Auth authenticate(AuthRequest request) {
        System.out.print(request);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();

            // verificar si existe un token para el usuario
            Optional<Token> existingTokenOptional = tokenRepository.findByUser(user);

            Token tokenEntity;
            String token;

            if (existingTokenOptional.isPresent()) {
                tokenEntity = existingTokenOptional.get();

                // verificar si el token ha expirado
                if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
                    // token expirado, genera uno nuevo
                    token = jwtService.getToken(user);
                    tokenEntity.setToken(token);
                    tokenEntity.setCreatedAt(LocalDateTime.now());
                    tokenEntity.setExpiresAt(LocalDateTime.now().plusMinutes(15));
                    System.out.println("Token expirado, nuevo token generado: " + token);
                } else {
                    // el token es valido entonces se usa el token existente
                    token = tokenEntity.getToken();
                    System.out.println("Token aún válido: " + token);
                }
            } else {
                // si no existe un token previo, se genera uno nuevo
                token = jwtService.getToken(user);
                tokenEntity = Token.builder()
                        .token(token)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusMinutes(15))
                        .user(user)
                        .build();

                System.out.println("No había un token, se generó uno nuevo: " + token);
            }


            tokenRepository.save(tokenEntity);

            return new Auth(token, user);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Email o contraseña inválido");
        } catch (DisabledException e) {
            throw new DisabledException("Cuenta no verificada");
        } catch (AuthenticationException e) {

            System.err.println("La autenticación falló: " + e.getMessage());
            throw new RuntimeException("La autenticación falló: " + e.getMessage());
        }
    }


    @Transactional
    public String confirm(String token) {

            System.out.println("Token recibido: " + token);
            // Ver si el token existe
            Token tokenFromDb = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new IllegalStateException("Token no encontrado"));

            User user = tokenFromDb.getUser();

        // Ver si el token expiro
            if (tokenFromDb.getExpiresAt().isBefore(LocalDateTime.now())) {

                User userToken = tokenFromDb.getUser();
                String generatedToken = jwtService.getToken(userToken);
                Token newToken = Token.builder()
                        .token(generatedToken)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusMinutes(15))
                        .user(tokenFromDb.getUser())
                        .build();

                tokenRepository.save(newToken);

                try {
                    emailService.sendEmail(
                            tokenFromDb.getUser().getNombre(),
                            tokenFromDb.getUser().getApellido(),
                            tokenFromDb.getUser().getEmail(),
                            "confirm-email",
                            String.format(CONFIRMATION_URL, generatedToken)
                    );
                    System.out.println("Email enviado");
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

                return "Token expirado. Un nuevo token ha sido enviado a tu email";
            }

            // Confirmar usuario
           // User user = tokenFromDb.getUser();
            user.setEnabled(true);
            userRepository.save(user);

            // Marcar el token como confirmado
            tokenFromDb.setConfirmedAt(LocalDateTime.now());
            tokenRepository.save(tokenFromDb);

            return "Su cuenta ha sido activada";
        }

    // Reenviar email de confirmacion y verificar si se modifico el email
    // falta modificar
    @Transactional
    public void resendEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));


        System.out.println("Usuario: " + user);

        String generatedToken = jwtService.getToken(user);
        Token token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepository.save(token);

        System.out.println("Token generado: " + generatedToken);
        // generar y enviar un nuevo email de confirmación
         confirm(generatedToken);
    }

   /*   @Transactional
    public void resendEmail(String token, String updatedEmail) {
      JwtParser parser = (JwtParser) Jwts.parser();
        try {
            Jws<Claims> jws = parser.parseClaimsJws(token);
            // Extract the payload claims
            Claims claims = jws.getBody();
            // Use the claims to authenticate the user or perform other actions
        } catch (Exception e) {
            // Handle the exception, e.g., throw a custom exception
            throw new IllegalStateException("Invalid token", e);
        }

        Token confirmationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token no encontrado"));

        // Ver si el token esta asociado a un usuario
        User user = confirmationToken.getUser();

        // Actualizar el email (si cambio)

        if (!user.getEmail().equals(updatedEmail)) {
            user.setEmail(updatedEmail);
            userRepository.save(user);
        }

       /* Si el token se encontró y esta asociado a un usuario,
       * se envia el mismo token a la funcion confirm para mandar el email y
       * darle un nuevo token */
/*
        confirm(String.valueOf(confirmationToken));
    }*/

    // OLVIDE CONTRASEÑA
    @Transactional
    public String forgotPasswordToken(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get(); //obtener usuario

            String generatedToken = jwtService.getToken(user); // generar y construir token

            Token newToken = Token.builder()
                    .token(generatedToken)
                    .createdAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusMinutes(15))
                    .user(user)
                    .build();

            tokenRepository.save(newToken);
            String resetUrl = String.format("http://localhost:8080/reset-password/%s", generatedToken);

            try {
                emailService.forgotPasswordEmail(
                        user.getNombre(),
                        user.getApellido(),
                        user.getEmail(),
                        "forgot-password",
                        resetUrl
                );
                return "El link para reestablecer su contraseña fue enviado." ;
            } catch (MessagingException e) {
                e.printStackTrace();
                return "Error enviado el email.";
            }
        } else {
            return "Email no encontrado.";
        }
    }


    // RESETEAR CONTRASEÑA
    @Transactional
    public void resetPassword(String token, String newPasswordDTO) {
        // buscar si existe el token
        Optional<Token> resetTokenOptional = tokenRepository.findByToken(token);
        if (resetTokenOptional.isPresent()) {
            //obtener info de user y token
            Token resetToken = resetTokenOptional.get();
            User user = resetToken.getUser();

            user.setPassword(passwordEncoder.encode(newPasswordDTO)); //nueva contraseña
            userRepository.save(user);
            tokenRepository.delete(resetToken);
        } else {
            throw new IllegalArgumentException("Token inválido");
        }
    }

    public void isEnabled(String email) {

        Optional<User> userExist = userRepository.findByEmail(email);
        if(userExist.isPresent() ){
            //ver si esta habilitado
            User nuevo = userExist.get();
            if(!nuevo.isEnabled()) {
                // si no esta
                String generatedToken = jwtService.getToken(nuevo); // generar y construir token
                Token newToken = Token.builder()
                        .token(generatedToken)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusMinutes(15))
                        .user(nuevo)
                        .build();

                tokenRepository.save(newToken);
                confirm(generatedToken);
            } else {
                forgotPasswordToken(email);
            }


        } else {
            // si no existe el email
            throw new IllegalArgumentException("Email no encontrado");
        }
    }


}
