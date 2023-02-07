package fr.loual.projectquizz.model.dtos;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PostedQuestion {

    private String quizzQuestion;
    private Boolean choiceType;
    private Boolean freeResponse;
    private List<String> tags;
    private List<PostedAnswer> answer;
    private Long quizzId;

}
