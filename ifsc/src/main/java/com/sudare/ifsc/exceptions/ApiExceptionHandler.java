package com.sudare.ifsc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//Tratador global de exceções para a API.
@RestControllerAdvice
public class ApiExceptionHandler {
    
    // Trata exceções de "não encontrado" e retorna HTTP 404 com uma mensagem simples
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex){
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
    
    // Trata erros de validação de DTOs (ex: campos obrigatórios)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex){
        Map<String, Object> body = new HashMap<>();
        body.put("error", "validation");
        
        // Monta um mapa com os campos inválidos e suas mensagens
        body.put("fields", ex.getBindingResult().getFieldErrors().stream()
                .collect(HashMap::new, (m,e)-> m.put(e.getField(), e.getDefaultMessage()), HashMap::putAll));
        return ResponseEntity.badRequest().body(body);
    }
}
