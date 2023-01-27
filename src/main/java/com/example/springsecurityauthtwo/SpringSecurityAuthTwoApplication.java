package com.example.springsecurityauthtwo;

import com.example.springsecurityauthtwo.security.model.entities.AppRole;
import com.example.springsecurityauthtwo.security.model.enumeration.ERole;
import com.example.springsecurityauthtwo.security.repositories.AppRoleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@Slf4j
@AllArgsConstructor
public class SpringSecurityAuthTwoApplication implements CommandLineRunner {


    private AppRoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityAuthTwoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        ERole[] erolesAr = {ERole.ROLE_ADMIN, ERole.ROLE_USER, ERole.ROLE_MODERATOR};
        List<ERole> listRoles = new ArrayList<>(List.of(erolesAr));
        listRoles.forEach(role -> {
            AppRole rol = new AppRole().setName(role);
            roleRepository.save(rol);
        });
    }
}
