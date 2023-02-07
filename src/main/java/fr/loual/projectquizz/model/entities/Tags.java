package fr.loual.projectquizz.model.entities;

import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor @NoArgsConstructor
public class Tags {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String tag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Tags tags = (Tags) o;
        return id != null && Objects.equals(id, tags.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
