package com.example.biblioteca.service.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
public class RegistrationRequest {
    @Getter @Setter
    private String nombre;

    @Getter @Setter
    private String apellido;

    @Getter @Setter
    private Long telefono;

    @Getter @Setter
    private String email;

    @Getter @Setter
    private String password;
}
