package fr.loual.projectquizz.repositories;

import fr.loual.projectquizz.model.entities.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface QuestionRepository extends CrudRepository<Question, Long> {

    List<Question> findByTagsTagContaining(String tag);
    List<Question> findAllByTagsTag(String tag);

}
