package fr.loual.projectquizz.security.model.dtos;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * Login request model object
 * @author Alexandre Lourencinho
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class LoginRequest {

    private String username;
    private String password;
}
