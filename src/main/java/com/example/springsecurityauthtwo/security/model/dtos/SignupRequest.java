package com.example.springsecurityauthtwo.security.model.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * Contains username, email, passsword and roles (usually empty array)
 * Eponymous function
 * @author Alexandre Lourencinho
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class SignupRequest {

    private String username;
    private String email;
    private String password;
    private Set<String> roles;

}
