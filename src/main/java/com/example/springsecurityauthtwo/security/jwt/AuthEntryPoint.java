package com.example.springsecurityauthtwo.security.jwt;

import com.example.springsecurityauthtwo.security.tools.SecurityConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexandre Lourencinho
 * @version 1.0
 */
@Slf4j
@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {


    /**
     * manage the entry points errors
     * @param request       that resulted in an <code>AuthenticationException</code>
     * @param response      so that the user agent can begin authentication
     * @param authException that caused the invocation
     * @throws IOException if I/O exception error occured
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("Unauthorized error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final String expired = (String) request.getAttribute(SecurityConstants.EXPIRED);
        final Map<String, Object> body = new HashMap<>();
        final ObjectMapper mapper = new ObjectMapper();
        String message;
        if(StringUtils.isNotEmpty(expired)) {
            message = expired;
            body.put(SecurityConstants.STATUS, HttpServletResponse.SC_UNAUTHORIZED);

        } else {
            message = authException.getMessage();
            body.put(SecurityConstants.STATUS, HttpServletResponse.SC_FORBIDDEN);
        }
        body.put(SecurityConstants.ERROR, SecurityConstants.EXPIRED);
        body.put(SecurityConstants.MESSAGE, message);
        body.put(SecurityConstants.PATH, request.getServletPath());
        mapper.writeValue(response.getOutputStream(), body);

    }

}
