package fr.loual.projectquizz.model.dtos;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class PostedAnswer {

    private String answer;
    private Long questionId;

}
