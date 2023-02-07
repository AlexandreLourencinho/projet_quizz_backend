package fr.loual.projectquizz;

import fr.loual.projectquizz.model.entities.Quizz;
import fr.loual.projectquizz.model.entities.Tags;
import fr.loual.projectquizz.repositories.QuizzRepository;
import fr.loual.projectquizz.repositories.TagsRepository;
import fr.loual.projectquizz.security.model.entities.AppRole;
import fr.loual.projectquizz.security.model.entities.AppUser;
import fr.loual.projectquizz.security.model.enumeration.ERole;
import fr.loual.projectquizz.security.repositories.AppRoleRepository;
import fr.loual.projectquizz.security.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


@SpringBootApplication
@Slf4j
@AllArgsConstructor
public class QuizzProjectApplication implements CommandLineRunner {


    private AppRoleRepository roleRepository;
    private QuizzRepository quizzRepository;
    private TagsRepository tagsRepository;
    private AppUserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(QuizzProjectApplication.class, args);
    }

    @Override
    public void run(String... args) {
        ERole[] erolesAr = {ERole.ROLE_ADMIN, ERole.ROLE_USER, ERole.ROLE_MODERATOR};
        List<ERole> listRoles = new ArrayList<>(List.of(erolesAr));
        listRoles.forEach(role -> {
            AppRole rol = new AppRole().setName(role);
            roleRepository.save(rol);
        });

        Iterable<AppRole> listRole = roleRepository.findAll();
        AtomicReference<AppRole> role = new AtomicReference<>();
        listRole.forEach(roleT -> {
            if(roleT.getName().toString().contains("USER")) {
                role.set(roleT);
                System.out.println(role);
            }
        });
        Set<AppRole> setRole = new HashSet<>();
        setRole.add(role.get());
        AppUser user = new AppUser().setEmail("aaa@email.fr")
                .setPassword("1234")
                .setUsername("alex")
                .setRoles(setRole);
        AppUser user1 = userRepository.save(user);
        Tags tag = new Tags().setTag("premier tag");
        Tags tagSaved = tagsRepository.save(tag);
        List<Tags> listTag = new ArrayList<>();
        listTag.add(tagSaved);
        Quizz quizz = new Quizz().setTags(listTag).setCommentary("premier commentaire")
                .setDescription("description 1").setUser(user1);
        quizzRepository.save(quizz);

        List<Quizz> quizzes2 = quizzRepository.findByTagsTagContaining("premier").orElse(new ArrayList<>());
        log.info("2" + quizzes2);

        List<Quizz> quizzes3 = quizzRepository.findAllByTagsTag("premier").orElse(new ArrayList<>());
        log.info("3" + quizzes3);

        List<Quizz> quizzes4 = quizzRepository.findAllByTagsTag(tag.getTag()).orElse(new ArrayList<>());

        log.info("4" + quizzes4);

    }
}
