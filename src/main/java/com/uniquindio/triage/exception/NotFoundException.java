package com.uniquindio.triage.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String mensaje) {
        super(mensaje);
    }
}