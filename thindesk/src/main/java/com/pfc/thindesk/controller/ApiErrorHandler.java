package com.pfc.thindesk.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiErrorHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String,Object>> naoEncontrado(NoSuchElementException ex) {
        return erro(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> invalido(IllegalArgumentException ex) {
        return erro(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> validacao(MethodArgumentNotValidException ex) {
        return erro(HttpStatus.BAD_REQUEST, "Dados inválidos: " + ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + " " + e.getDefaultMessage()).findFirst().orElse("verifique os campos"));
    }
    private ResponseEntity<Map<String,Object>> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of(
            "codigo", status.value(), "mensagem", mensagem, "instante", LocalDateTime.now().toString()));
    }
}
