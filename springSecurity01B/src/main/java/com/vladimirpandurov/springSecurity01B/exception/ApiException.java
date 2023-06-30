package com.vladimirpandurov.springSecurity01B.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
