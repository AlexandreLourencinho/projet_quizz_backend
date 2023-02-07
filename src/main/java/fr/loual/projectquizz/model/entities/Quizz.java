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
public class Quizz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String commentary;
    @ManyToMany
    @JoinTable(name = "quizz_tags",
            joinColumns = @JoinColumn(name = "quizz_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @ToString.Exclude
    private List<Tags> tags;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private AppUser user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Quizz quizz = (Quizz) o;
        return id != null && Objects.equals(id, quizz.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
