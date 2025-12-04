package com.sudare.ifsc.exceptions;

// Exceção personalizada usada quando um recurso não é encontrado na API.
// É lançada no service e tratada pelo ApiExceptionHandler.
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
