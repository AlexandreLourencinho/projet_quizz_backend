package fr.loual.projectquizz.security.repositories;

import fr.loual.projectquizz.security.model.entities.AppRole;
import fr.loual.projectquizz.security.model.enumeration.ERole;
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
