package com.example.springsecurityauthtwo.security.repositories;

import com.example.springsecurityauthtwo.security.model.entities.AppRole;
import com.example.springsecurityauthtwo.security.model.enumeration.ERole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Alexandre Lourencinho
 * @version 1.0
 */
@Repository
public interface AppRoleRepository extends CrudRepository<AppRole, Long> {

        Optional<AppRole> findByName(ERole name);
}
