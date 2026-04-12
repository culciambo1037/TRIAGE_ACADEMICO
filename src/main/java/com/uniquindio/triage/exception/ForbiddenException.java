package com.uniquindio.triage.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String mensaje) {
        super(mensaje);
    }
}