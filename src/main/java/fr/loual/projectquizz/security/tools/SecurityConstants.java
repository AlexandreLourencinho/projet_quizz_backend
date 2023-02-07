package fr.loual.projectquizz.security.tools;

/**
 * Constants used in security package
 * @author Alexandre Lourencinho
 * @version 1.0
 */
public class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String ERROR_ROLE = "Error: role not found";
    public static final String HEADER_TOKEN = "Authorization";
    public static final String TOKEN_START = "Bearer ";
    public static final String TOKEN_START_REFRESH = "Refresh ";
    public static final String REFRESH_TOKEN = "Refresh";
    public static final String STATUS = "status";
    public static final String ERROR = "error";
    public static final String SUCCESS = "success";
    public static final String MESSAGE = "message";
    public static final String PATH = "path";
    public static final String EXPIRED = "expired";
    public static final String ERROR_USERNAME_TAKEN = "Error : Username is already taken !";
    public static final String ERROR_MAIL_TAKEN = "Error : Email is already use !";
    public static final String INVALID_REFRESH = "Refresh token is invalid";


    public static final int BEARER_SUBSTRING = 7;
    public static final int REFRESH_SUBSTRING = 8;
}
