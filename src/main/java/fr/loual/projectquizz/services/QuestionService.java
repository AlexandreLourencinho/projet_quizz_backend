package fr.loual.projectquizz.services;

import fr.loual.projectquizz.model.dtos.PostedQuestion;
import fr.loual.projectquizz.model.entities.Question;
import fr.loual.projectquizz.repositories.QuestionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class QuestionService {


    private QuestionRepository questionRepository;
    private ModelMapper mapper;


    public void saveQuestion(PostedQuestion question) {
        Question savingQuestion = mapper.map(question, Question.class);
        questionRepository.save(savingQuestion);
        log.info("question saved");
    }

    public Question findQuestionById(Long questionId) {
        return questionRepository.findById(questionId).orElse(null);
    }

    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }

}
