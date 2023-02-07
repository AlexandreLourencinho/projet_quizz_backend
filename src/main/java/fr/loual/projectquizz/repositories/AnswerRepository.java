package fr.loual.projectquizz.repositories;


import fr.loual.projectquizz.model.entities.Answer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends CrudRepository<Answer, Long> {
}
