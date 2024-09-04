package com.example.biblioteca.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    @Getter
    @Setter
    private String errorCode;

    @Getter
    @Setter
    private String errorMessage;
}
