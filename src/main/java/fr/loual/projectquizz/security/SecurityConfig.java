package fr.loual.projectquizz.security;

import fr.loual.projectquizz.security.jwt.AuthEntryPoint;
import fr.loual.projectquizz.security.jwt.AuthTokenFilter;
import fr.loual.projectquizz.security.jwt.JwtUtils;
import fr.loual.projectquizz.security.model.enumeration.ERole;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Security configuration class
 *
 * @author Alexandre Lourencinho
 * @version 1.0
 */
@Configuration
@AllArgsConstructor
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private UserDetailsService userDetailsService;
    private AuthEntryPoint unhauthorizedHandler;
    private JwtUtils jwtUtils;

    /**
     * Bean providing the auth provider
     *
     * @return a DaeoAuthenticationProvider object
     */
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * Bean returning the authentication manager
     *
     * @param authConfig AuthenficationConfiguration object
     * @return the authentication manager
     * @throws Exception if something went bad
     */
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Bean returning the Entry point filter
     *
     * @return the filter
     */
    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    /**
     * Bean providing the password encoder
     *
     * @return a BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * The filter chain applied to the routes. Default permitted : signup, signin. h2 is present for dev purpose.
     * Defines the filters, the auth provider that will be used
     *
     * @param http the HttpSecurity object
     * @return a SecurityFilterChain
     * @throws Exception is something went wrong
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // managing  cors, csrf, stateless policy and allowed url
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unhauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/user/signup/**", "/user/signing/**", "/h2-console/**", "/user/refreshToken", "/actuator/health", "/actuator/beans", "/actuator/env").permitAll();
        http.authorizeRequests().antMatchers("/actuator/beans", "/actuator/env").hasRole(ERole.ROLE_ACTUATOR.toString().replace("ROLE_", ""));
        http.authorizeRequests().anyRequest().authenticated();
        http.headers().frameOptions().sameOrigin();
        http.authenticationProvider(authProvider());
        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
