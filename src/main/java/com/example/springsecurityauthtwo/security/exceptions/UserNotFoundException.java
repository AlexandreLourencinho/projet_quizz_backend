package com.example.springsecurityauthtwo.security.exceptions;

/**
 * Exception for user not found
 * @author Alexandre Lourencinho
 * @version 1.0
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }

}
