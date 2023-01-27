package com.example.springsecurityauthtwo.security.exceptions;

/**
 * Exception for role not found
 * @author Alexandre Lourencinho
 * @version 1.0
 */
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String message) {
        super(message);
    }

}
