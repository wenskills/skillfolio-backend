package app.dao;

import app.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("open")
class ActivityRepositoryTest {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Test
    void testCreateAndReadActivity() {
        Person person = new Person();
        person.setFirstName("Jane");
        person.setLastName("Doe");
        person.setEmail("jane@doe.com");
        person.setPassword("password123");
        person.setBirthDate(LocalDate.of(1992, 1, 10));
        personRepository.save(person);

        Resume resume = new Resume();
        resume.setOwner(person);
        resume.setTitle("CV principal");
        resumeRepository.save(resume);

        Activity activity = new Activity();
        activity.setYear(2024);
        activity.setNature(ActivityNature.PROJET);
        activity.setTitle("Développement Application JEE");
        activity.setDescription("Mini projet Spring Boot - Gestion de CVs");
        activity.setWebAddress("https://github.com/janejee");
        activity.setResume(resume);
        activityRepository.save(activity);

        Optional<Activity> found = activityRepository.findById(activity.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Développement Application JEE");
        assertThat(found.get().getResume().getOwner().getEmail()).isEqualTo("jane@doe.com");
    }

    @Test
    void testUpdateActivity() {
        Person p = new Person();
        p.setFirstName("Max");
        p.setLastName("Payne");
        p.setEmail("max@ex.com");
        p.setPassword("password123");
        personRepository.save(p);

        Resume resume = new Resume();
        resume.setOwner(p);
        resume.setTitle("CV principal");
        resumeRepository.save(resume);

        Activity a = new Activity();
        a.setYear(2023);
        a.setNature(ActivityNature.EXPERIENCE);
        a.setTitle("Stage Java");
        a.setDescription("Développement backend Spring");
        a.setResume(resume);
        activityRepository.save(a);

        a.setTitle("Stage Spring Boot");
        activityRepository.save(a);

        Activity updated = activityRepository.findById(a.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Stage Spring Boot");
    }

    @Test
    void testDeleteActivity() {
        Person p = new Person();
        p.setFirstName("Anna");
        p.setLastName("Smith");
        p.setEmail("anna@ex.com");
        p.setPassword("password123");
        personRepository.save(p);

        Resume resume = new Resume();
        resume.setOwner(p);
        resume.setTitle("CV principal");
        resumeRepository.save(resume);

        Activity a = new Activity();
        a.setYear(2022);
        a.setNature(ActivityNature.FORMATION);
        a.setTitle("Master Informatique");
        a.setDescription("Université de Paris");
        a.setResume(resume);
        activityRepository.save(a);

        activityRepository.delete(a);

        Optional<Activity> deleted = activityRepository.findById(a.getId());
        assertThat(deleted).isEmpty();
    }
}
