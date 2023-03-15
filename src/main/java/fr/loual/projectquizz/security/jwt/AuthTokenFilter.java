package fr.loual.projectquizz.security.jwt;

import fr.loual.projectquizz.security.tools.SecurityConstants;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Alexandre Lourencinho
 * @version 1.0
 */
@Slf4j
@AllArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private JwtUtils jwtUtils;
    private UserDetailsService userDetailsService;

    /**
     * internal filter for request management
     *
     * @param request     the HttpServletRequest object
     * @param response    the HttpServletResponse object
     * @param filterChain FilterChain object provided by the servlet container giving a view into the invocation chain of a filtered request for a resource.
     * @throws ServletException Exception that can be thrown by the Servlet
     * @throws IOException      base exceptions which occur while reading or accessing files, directories and streams
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestToken = request.getHeader(SecurityConstants.HEADER_TOKEN);
        if (StringUtils.startsWith(requestToken, SecurityConstants.TOKEN_START)) {
            String jwt = requestToken.substring(SecurityConstants.BEARER_SUBSTRING);
            try {
                String username = jwtUtils.getUsernameFromToken(jwt);
                if (StringUtils.isNotEmpty(username)
                        &&
                        null == SecurityContextHolder.getContext().getAuthentication()) {
                    UserDetails user = userDetailsService.loadUserByUsername(username);
                    if (Boolean.TRUE.equals(jwtUtils.validateToken(jwt, user))) {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (IllegalArgumentException e) {
                log.error("Unable to fetch JWT Token");
            } catch (ExpiredJwtException e) {
                manageExpiredToken(request, e);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        } else {
            log.warn("JWT token does not begin with Bearer String ");
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Expired jxt management
     *
     * @param request the httpservletrequest object
     * @param e       the ExpiredException error
     */
    public void manageExpiredToken(HttpServletRequest request, ExpiredJwtException e) {
        log.error("JWT Token is expired");
        request.setAttribute(SecurityConstants.EXPIRED, e.getMessage());
        // get username and comparing refresh jwt with actual datetime
    }

}
