package com.example.springsecurityauthtwo.security.controllers;

import com.example.springsecurityauthtwo.security.jwt.JwtUtils;
import com.example.springsecurityauthtwo.security.model.dtos.*;
import com.example.springsecurityauthtwo.security.model.entities.*;
import com.example.springsecurityauthtwo.security.model.enumeration.ERole;
import com.example.springsecurityauthtwo.security.services.UserDetailsImpl;
import com.example.springsecurityauthtwo.security.services.UserServices;
import com.example.springsecurityauthtwo.security.tools.SecurityConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the authentification, account creation, role management, etc
 * @author Alexandre Lourencinho
 * @version 1.0
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user")
@Slf4j
public class AuthController {

    private AuthenticationManager manager;
    private UserServices userServices;
    private PasswordEncoder passwordEncoder;
    private UserDetailsService userDetailsService;
    private JwtUtils jwtUtils;

    /**
     * Read - Path allowing user to connect
     * @param loginRequest the login request that contains username and password
     * @return users infos with access and refresh token
     */
    @PostMapping("/signin")
    public ResponseEntity<UserInfoResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // context and auth management
        log.info("User signin in...");
        Authentication auth = manager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        UserDetails userDetails = (UserDetailsImpl) auth.getPrincipal();

        // jwt generation
        Map<String, String> tokens = jwtUtils.generateTokenAndRefresh(userDetails.getUsername());
        HttpHeaders headers = new HttpHeaders();
        headers.add(SecurityConstants.HEADER_TOKEN, SecurityConstants.TOKEN_START + tokens.get("token"));
        headers.add(SecurityConstants.REFRESH_TOKEN, SecurityConstants.TOKEN_START_REFRESH + tokens.get("refreshToken"));
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        // build and send response
        UserInfoResponse infoResponse = new UserInfoResponse(userDetails.getUsername(), roles);
        log.info("user signed in");
        return new ResponseEntity<>(infoResponse, headers, HttpStatus.OK);
    }

    /**
     * Write - Path allowing a user to create an account
     * @param signupRequest Object that contains username mail password and roles. default role is user
     * @return A string that signify if the user is created or not
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody SignupRequest signupRequest) {

        log.warn("registering a new user...");
        Map<String, Object> responseBody = new HashMap<>();

        // check if username already taken
        if (Boolean.TRUE.equals(userServices.usernameAlreadyExists(signupRequest.getUsername()))) {
            log.warn(SecurityConstants.ERROR_USERNAME_TAKEN);
            responseBody.put(SecurityConstants.ERROR, SecurityConstants.ERROR_USERNAME_TAKEN);
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(responseBody);
        }

        // check if email already taken
        if (Boolean.TRUE.equals(userServices.emailAlreadyExists(signupRequest.getEmail()))) {
            log.warn(SecurityConstants.ERROR_MAIL_TAKEN);
            responseBody.put(SecurityConstants.ERROR, SecurityConstants.ERROR_MAIL_TAKEN);
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(responseBody);
        }

        AppUser user = new AppUser()
                .setUsername(signupRequest.getUsername())
                .setEmail(signupRequest.getEmail())
                .setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        // check user's role
        Set<String> strRoles = signupRequest.getRoles();
        Set<AppRole> roles = new HashSet<>();
        if (strRoles == null) {
            AppRole userRoles = userServices.findRoleByRolename(ERole.ROLE_USER);
            roles.add(userRoles);
        } else {
            userServices.manageRoles(strRoles, roles);
        }

        user.setRoles(roles);
        userServices.saveNewUser(user);
        log.info("user registered successfully!");
        responseBody.put(SecurityConstants.SUCCESS, String.format("User %s registered successfully", user.getUsername()));
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
    }

    /**
     * read - generate a new access token using the refresh token
     * @param request the httpservlet request object
     * @return a http response with the new token or the error
     */
    @GetMapping("/refreshToken")
    public ResponseEntity<Map<String, Object>> refreshToken(HttpServletRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        // get jwt token from request header
        String headerRefreshToken = request.getHeader(SecurityConstants.REFRESH_TOKEN);

        // check if token exists
        if (StringUtils.isEmpty(headerRefreshToken)) {
            responseBody.put(SecurityConstants.ERROR, "no refresh token provided");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body(responseBody);
        }

        try {

            String refreshToken = headerRefreshToken.substring(SecurityConstants.REFRESH_SUBSTRING);
            String username = jwtUtils.getUsernameFromToken(refreshToken);
            UserDetails user = userDetailsService.loadUserByUsername(username);

            // check if token is valid, send error otherwise
            if (Boolean.FALSE.equals(jwtUtils.validateToken(refreshToken, user))) {
                responseBody.put(SecurityConstants.ERROR, SecurityConstants.INVALID_REFRESH);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body(responseBody);
            }

            // response with token
            String newAccessToken = jwtUtils.generateJwtToken(username);
            responseBody.put("newToken", SecurityConstants.TOKEN_START + newAccessToken);
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);

        } catch (Exception e) {
            responseBody.put(SecurityConstants.ERROR, SecurityConstants.INVALID_REFRESH);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body(responseBody);
        }
    }

    /**
     * write - update an existing user
     * @param userDto the user info to be update
     * @param request the HttpServlet Request
     * @return the modified user or an error
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser(@Valid @RequestBody SignupRequest userDto, HttpServletRequest request) {
        Map<String, Object> responseBody = new HashMap<>();

        // get username from jwt and update its informations
        String username = jwtUtils.getUsernameFromToken(request.getHeader(SecurityConstants.HEADER_TOKEN).substring(SecurityConstants.BEARER_SUBSTRING));
        AppUser updatedUser = userServices.updateUserInfo(userDto, username);

        if (Objects.nonNull(updatedUser)) {
            List<String> roles = updatedUser.getRoles().stream()
                    .map(role -> String.valueOf(role.getName()))
                    .toList();

            // build a response with updated user info
            UserInfoResponse responseInfo = new UserInfoResponse().setUsername(updatedUser.getUsername())
                    .setRoles(roles);
            if (!Objects.equals(username, userDto.getUsername())) {
                String newToken = jwtUtils.generateJwtToken(userDto.getUsername());
                responseBody.put("token", newToken);
            }
            responseBody.put("data", responseInfo);
            return ResponseEntity.ok(responseBody);
        } else {
            responseBody.put(SecurityConstants.ERROR, "Could not update user.");
            return ResponseEntity.badRequest().body(responseBody);
        }
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public void testController() {
    }

}