package com.example.biblioteca.controller;

import com.example.biblioteca.dto.UserDTO;
import com.example.biblioteca.entity.User;
import com.example.biblioteca.service.AuthService;
import com.example.biblioteca.service.UserService;
import com.example.biblioteca.service.csv.ExportCsv;
import com.example.biblioteca.service.request.Auth;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/user/")
public class UserC {

    private final AuthService service;
    private final UserService userService;

    @Autowired
    ExportCsv exportCsv;

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.unUser(id);
    }

    //VER USUARIOS
    @GetMapping("/list")
    public List<User> verUsuarios(){
        return userService.verUsuarios();
    }

    // VER USUARIOS ACTIVOS
    @GetMapping("/active-list")
    public List<User> verActivos(){
        return userService.verUserActivos();
    }
    // GUARDAR USUARIO
    @PostMapping(path = "/save")
    public Auth guardarUser(@RequestBody User u) {
        return userService.guardarUser(u);
    }
    // GUARDAR USUARIO x ADMIN
    @PostMapping(path = "/saveUser")
    public User guardarUserxAdmin(@RequestBody User u) {
        return userService.guardarUserxAdmin(u);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User nuevo) {
        try {
            userService.updateUser(id, nuevo);
            return ResponseEntity.ok("Usuario modificado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }


    // ELIMINAR USUARIO

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
    // CAMBIAR ROL DE USER -> ADMIN

    @PutMapping("/role/{id}")
    public ResponseEntity<String> updateUserRole(@PathVariable Long id) {
        userService.updateUserRole(id);
        return ResponseEntity.ok("Se ha enviado la solicitud.");
    }

    // exportar csv
    @GetMapping("/exportCsv")
    public void exportUserCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"usuarios-biblioteca.csv\"");

        exportCsv.userCsv(userService.verUsuarios(), response.getOutputStream());
    }

}
