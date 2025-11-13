package com.williammedina.token_gateway.infraestructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class AppException extends RuntimeException{
    private final String message;
    private final HttpStatus httpStatus;
}
