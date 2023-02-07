package fr.loual.projectquizz.repositories;

import fr.loual.projectquizz.model.entities.Quizz;
import fr.loual.projectquizz.security.model.entities.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizzRepository extends CrudRepository<Quizz, Long> {

    Optional<List<Quizz>> findAllByUser(AppUser user);
    Optional<Quizz> findById(Long id);
    Optional<List<Quizz>> findByTagsTagContaining(String tag);
    Optional<List<Quizz>> findAllByTagsTag(String tag);
}
