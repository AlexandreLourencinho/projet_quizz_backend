package fr.loual.projectquizz.services;

import fr.loual.projectquizz.model.dtos.PostedQuizz;
import fr.loual.projectquizz.model.entities.Quizz;
import fr.loual.projectquizz.repositories.QuizzRepository;
import fr.loual.projectquizz.security.model.entities.AppUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
public class QuizzService {

    private QuizzRepository quizzRepository;
    private ModelMapper mapper;

    public void saveQuizz(PostedQuizz quizz) {
        Quizz savingQuizz = mapper.map(quizz, Quizz.class);
        quizzRepository.save(savingQuizz);
        log.info("quizz saved");
    }

    public void deleteQuizz(Long quizzId) {
        quizzRepository.deleteById(quizzId);
    }

    public Quizz findQuizzById(Long quizzId) {
        return quizzRepository.findById(quizzId).orElse(null);
    }

    public List<Quizz> findAllQuizzByUser(AppUser user) {
        return quizzRepository.findAllByUser(user).orElse(null);
    }

}
