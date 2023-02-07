package fr.loual.projectquizz.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

/**
 * * Tools used for the JWT management
 * @author Alexandre Lourencinho
 * @version 1.0
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${fr.loual.jwtSecret}")
    private String secret;
    @Value("${fr.loual.minuteExpiration}")
    private String minuteExpiration;


    /**
     * Method generating access and refresh token
     * @param username the user's username
     * @return a Map containing both tokens
     */
    public Map<String, String> generateTokenAndRefresh(String username) {
        log.info("generating new Access Token and new Refresh Token...");
        String refreshToken = generateJwtRefreshToken(username);
        String token = generateJwtToken(username);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("token", token);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    /**
     * Creation method for the access token
     * @param username the user's username
     * @return a string jwt
     */
    public String generateJwtToken(String username) {
        log.info("generating access token...");
        Map<String, Object> claims = new HashMap<>();
        Date date = new Date(System.currentTimeMillis());
        Date expDate = Date.from(LocalDateTime.now().plusMinutes(Long.parseLong(minuteExpiration)).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(date)
                .setExpiration(expDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * Creation method for the refresh token
     * @param username the user's username
     * @return a string jwt
     */
    public String generateJwtRefreshToken(String username) {
        log.info("generating refresh token...");
        Date date = new Date(System.currentTimeMillis());
        Map<String, Object> claims = new HashMap<>();
        Date refreshDate = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(date)
                .setExpiration(refreshDate)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }


    /**
     * Get all claims from a JWT Token
     * @param token the jwt token, access or refresh
     * @return Claims object
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * Get a specific claim from a Claims object
     * @param token the JWT token
     * @param claimsResolver a function
     * @param <T> claim type, expiration date, subject..
     * @return a claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * get username from the JWT token claims
     * @param token jwt token
     * @return a string username
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * get token expiration date
     * @param token the jwt token
     * @return a date type
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * check if token is expired
     * @param token the jwt token
     * @return true or false (or null)
     */
    private Boolean isTokenExpired(String token) {
        final Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    /**
     * check if the jwt token is valid
     * @param token the jwt token
     * @param user the user associated with
     * @return true false or null
     */
    public Boolean validateToken(String token, UserDetails user) {
        log.info("checking if token is still valid...");
        final String username = getUsernameFromToken(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

}
