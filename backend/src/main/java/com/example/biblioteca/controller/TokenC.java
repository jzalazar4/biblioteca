package com.example.biblioteca.controller;

import com.example.biblioteca.entity.Token;
import com.example.biblioteca.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/token")
public class TokenC {
    @Autowired
    TokenService tokenService;

    @GetMapping("/getToken")
    public ResponseEntity<Token> getToken(@RequestParam("token") String token) {
        Optional<Token> tokenOptional = tokenService.findByToken(token);

        if (tokenOptional.isPresent()) {
            Token tokenFound = tokenOptional.get();
            return ResponseEntity.ok(tokenFound); //devuelve con 200 ok
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404
                    .body(null);
        }
    }
}
