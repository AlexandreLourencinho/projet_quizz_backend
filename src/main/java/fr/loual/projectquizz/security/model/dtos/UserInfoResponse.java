package fr.loual.projectquizz.security.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * User info that will be sent after a successful login request
 * @author Alexandre Lourencinho
 * @version 1.0
 */
@Data
@AllArgsConstructor @NoArgsConstructor
@Accessors(chain = true)
public class UserInfoResponse {

    private String username;
    private List<String> roles;

}
