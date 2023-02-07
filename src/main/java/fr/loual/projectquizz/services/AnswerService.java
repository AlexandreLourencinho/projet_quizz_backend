package fr.loual.projectquizz.services;

import fr.loual.projectquizz.model.dtos.PostedAnswer;
import fr.loual.projectquizz.model.entities.Answer;
import fr.loual.projectquizz.repositories.AnswerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AnswerService {

    private AnswerRepository answerRepository;
    private ModelMapper mapper;

    public Answer findAnswerById(Long answerId) {
        return answerRepository.findById(answerId).orElse(null);
    }

    public void saveAnswer(PostedAnswer answer) {
        Answer savingAnswer = mapper.map(answer, Answer.class);
        answerRepository.save(savingAnswer);
        log.info("Answer saved");
    }

    public void deleteAnswer(Long answerId) {
        answerRepository.deleteById(answerId);
    }

}
