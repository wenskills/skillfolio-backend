package app.config;

import app.dao.ActivityRepository;
import app.dao.PersonRepository;
import app.dao.ResumeRepository;
import app.model.Activity;
import app.model.ActivityNature;
import app.model.Person;
import app.model.Resume;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/***********
 * Initialisation de notre base de donnée
 * => utilisation de faker pour génerer les données:
 * ====> utilisateurs : nom, prénom, website, birthdate
 * ====> resume (cv): "CV principal"
 * ====> activity : title, description
 *
 * => Metrics: 100_000 personnes, avec 2 à 5 activités par personnes
 * ***************************/
@Component
@Profile("usejwt")
public class DataInitializer {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PasswordEncoder encoder;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    private static final int TOTAL = 10;
    private static final int BATCH_SIZE = 5;

    @PostConstruct
    public void initData() {
        long start = System.currentTimeMillis();

        String hashedPassword = encoder.encode("Password123!");

        List<Person> personsBatch = new ArrayList<>(BATCH_SIZE);
        List<Resume> resumesBatch = new ArrayList<>(BATCH_SIZE);
        List<Activity> actsBatch = new ArrayList<>(BATCH_SIZE * 4);

        System.out.println("Génération de " + TOTAL + " personnes");
        System.out.println("Chargement en cours, merci de patienter...");

        for (int i = 0; i < TOTAL; i++) {

            Person p = new Person();
            p.setFirstName(faker.name().firstName());
            p.setLastName(faker.name().lastName());
            p.setEmail("user" + i + "@test.com");
            p.setWebsite("https://" + faker.internet().url());
            p.setBirthDate(LocalDate.of(
                    1970 + random.nextInt(30),
                    1 + random.nextInt(12),
                    1 + random.nextInt(28)
            ));
            p.setPassword(hashedPassword);

            personsBatch.add(p);

            if (personsBatch.size() == BATCH_SIZE) {

                // 1) Save persons
                List<Person> savedPersons = personRepository.saveAll(personsBatch);

                // 2) Create + save resumes (CV principal)
                resumesBatch.clear();
                for (Person saved : savedPersons) {
                    Resume r = new Resume();
                    r.setOwner(saved);
                    r.setTitle("CV principal");
                    r.setTemplate("default");
                    resumesBatch.add(r);
                }
                List<Resume> savedResumes = resumeRepository.saveAll(resumesBatch);

                // 3) Create activities and attach to the corresponding resume
                actsBatch.clear();
                for (int idx = 0; idx < savedPersons.size(); idx++) {
                    Resume resume = savedResumes.get(idx);

                    int nActs = 2 + random.nextInt(4);
                    for (int j = 0; j < nActs; j++) {
                        Activity a = new Activity();
                        a.setResume(resume); // ✅ au lieu de setPerson(saved)
                        a.setYear(2000 + random.nextInt(25));
                        a.setNature(randomNature());
                        a.setTitle(faker.job().title());
                        a.setDescription(faker.lorem().sentence());
                        a.setWebAddress(faker.internet().url());
                        a.setPosition(j);

                        actsBatch.add(a);
                    }
                }

                activityRepository.saveAll(actsBatch);

                personsBatch.clear();
            }
        }

        // Si TOTAL n'est pas multiple de BATCH_SIZE, gérer le reste
        if (!personsBatch.isEmpty()) {
            List<Person> savedPersons = personRepository.saveAll(personsBatch);

            resumesBatch.clear();
            for (Person saved : savedPersons) {
                Resume r = new Resume();
                r.setOwner(saved);
                r.setTitle("CV principal");
                r.setTemplate("default");
                resumesBatch.add(r);
            }
            List<Resume> savedResumes = resumeRepository.saveAll(resumesBatch);

            actsBatch.clear();
            for (int idx = 0; idx < savedPersons.size(); idx++) {
                Resume resume = savedResumes.get(idx);

                int nActs = 2 + random.nextInt(4);
                for (int j = 0; j < nActs; j++) {
                    Activity a = new Activity();
                    a.setResume(resume);
                    a.setYear(2000 + random.nextInt(25));
                    a.setNature(randomNature());
                    a.setTitle(faker.job().title());
                    a.setDescription(faker.lorem().sentence());
                    a.setWebAddress(faker.internet().url());
                    a.setPosition(j);
                    actsBatch.add(a);
                }
            }

            activityRepository.saveAll(actsBatch);
            personsBatch.clear();
        }

        long duration = System.currentTimeMillis() - start;
        System.out.println("Génération terminée en " + (duration / 1000.0) + " sec.");
    }

    private ActivityNature randomNature() {
        ActivityNature[] values = ActivityNature.values();
        return values[random.nextInt(values.length)];
    }
}
