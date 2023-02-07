package fr.loual.projectquizz.model.entities;

import fr.loual.projectquizz.security.model.entities.AppUser;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Question {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String quizzQuestion;
    private Boolean choiceType;
    private Boolean freeResponse;
    @ManyToMany
    @ToString.Exclude
    @JoinTable(name = "question_tags",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tags> tags;
    @OneToMany(mappedBy = "question")
    @ToString.Exclude
    private List<Answer> answer;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private AppUser user;
    @ManyToOne
    @JoinColumn(name = "quizz_id")
    private Quizz quizz;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Question question = (Question) o;
        return id != null && Objects.equals(id, question.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
