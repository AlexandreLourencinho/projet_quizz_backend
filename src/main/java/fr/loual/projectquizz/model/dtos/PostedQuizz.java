package fr.loual.projectquizz.model.dtos;

import fr.loual.projectquizz.security.model.entities.AppUser;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@Accessors(chain = true)
public class PostedQuizz {

    @Nullable
    private AppUser user;
    private String name;
    private String description;
    private String commentary;
    private List<String> tags;
}
