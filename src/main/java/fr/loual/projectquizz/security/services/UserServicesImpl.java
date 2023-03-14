package fr.loual.projectquizz.security.services;

import fr.loual.projectquizz.security.exceptions.RoleNotFoundException;
import fr.loual.projectquizz.security.exceptions.UserNotFoundException;
import fr.loual.projectquizz.security.model.dtos.SignupRequest;
import fr.loual.projectquizz.security.model.entities.AppRole;
import fr.loual.projectquizz.security.model.entities.AppUser;
import fr.loual.projectquizz.security.model.enumeration.ERole;
import fr.loual.projectquizz.security.repositories.AppRoleRepository;
import fr.loual.projectquizz.security.repositories.AppUserRepository;
import fr.loual.projectquizz.security.tools.SecurityConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * The class UserServicesImpl which implements the interface UserServices.
 *
 * @author Alexandre
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserServicesImpl implements UserServices {

    private final AppUserRepository userRepository;
    private final AppRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUser findUserByUsername(String username) {
        log.info("retievring AppUser from his username...");
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public Boolean usernameAlreadyExists(String username) {
        log.info("checking if username is already taken...");
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean emailAlreadyExists(String email) {
        log.info("checking if email is already taken...");
        return userRepository.existsByEmail(email);
    }

    @Override
    public AppRole findRoleByRolename(ERole rolename) {
        log.info("retrieving the role...");
        return roleRepository.findByName(rolename).orElse(null);
    }

    @Override
    public AppUser saveNewUser(AppUser user) {
        log.info("registering a new user...");
        return userRepository.save(user);
    }

    @Override
    public void manageRoles(Set<String> strRoles, Set<AppRole> roles) {
        log.info("managing new user's roles...");
        strRoles.forEach(role -> {
            switch (role) {
                case "admin":
                    AppRole adminRole = findRoleByRolename(ERole.ROLE_ADMIN);
                    if (adminRole == null) {
                        throw new RoleNotFoundException(SecurityConstants.ERROR_ROLE);
                    }
                    roles.add(adminRole);
                    break;
                case "mod":
                    AppRole modRole = findRoleByRolename(ERole.ROLE_MODERATOR);
                    if (modRole == null) {
                        throw new RoleNotFoundException(SecurityConstants.ERROR_ROLE);
                    }
                    roles.add(modRole);
                    break;
                default:
                    AppRole userRole = findRoleByRolename(ERole.ROLE_USER);
                    if (userRole == null) {
                        throw new RoleNotFoundException(SecurityConstants.ERROR_ROLE);
                    }
                    roles.add(userRole);
                    break;
            }
        });
    }

    @Override
    public AppUser findById(Long id) {
        log.info("finding a user through it's id....");
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public AppUser updateUserInfo(SignupRequest user, String username) {
        log.info("updating user...");
        AppUser oldUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User", "username", username));
        Set<AppRole> enumRoles = user.getRoles().stream()
                .map(role -> roleRepository.findByName(ERole.valueOf(role))
                        .orElseThrow(() -> new UserNotFoundException("Role", "name", role)))
                .collect(Collectors.toSet());

        oldUser.setUsername(user.getUsername())
                .setPassword(this.passwordEncoder.encode(user.getPassword()))
                .setRoles(enumRoles)
                .setEmail(user.getEmail());
        return userRepository.save(oldUser);
    }

    @Override
    public void deleteUser(AppUser user) {
        log.info("deleting user" + user.getUsername() + "....");
        userRepository.delete(user);
    }

}
