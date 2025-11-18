package app.service;

import app.dao.PersonRepository;
import app.model.Activity;
import app.model.ActivityNature;
import app.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("open")
class ActivityServiceTest {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private PersonRepository personRepository;

    @Test
    void testCreateAndFindActivity() {
        Person person = new Person();
        person.setFirstName("Emma");
        person.setLastName("Watson");
        person.setEmail("emma@example.com");
        person.setPassword("password123");
        person.setBirthDate(LocalDate.of(1990, 1, 1));
        personRepository.save(person);

        Activity a = new Activity();
        a.setYear(2024);
        a.setNature(ActivityNature.PROJET);
        a.setTitle("Développement FullStack");
        a.setDescription("Projet universitaire complet");
        a.setPerson(person);
        activityService.create(a);

        Activity found = activityService.findById(a.getId()).orElseThrow();
        assertThat(found.getTitle()).isEqualTo("Développement FullStack");
        assertThat(found.getPerson().getEmail()).isEqualTo("emma@example.com");
    }

    @Test
    void testUpdateActivity() {
        Person p = new Person();
        p.setFirstName("Max");
        p.setLastName("Planck");
        p.setEmail("max@example.com");
        p.setPassword("password123");
        personRepository.save(p);

        Activity a = new Activity();
        a.setYear(2023);
        a.setNature(ActivityNature.EXPERIENCE);
        a.setTitle("Stage en Java");
        a.setDescription("Développement backend");
        a.setPerson(p);
        activityService.create(a);

        a.setTitle("Stage en Spring Boot");
        Activity updated = activityService.update(a.getId(), a);

        assertThat(updated.getTitle()).isEqualTo("Stage en Spring Boot");
    }

    @Test
    void testSearchByTitle() {
        Person p = new Person();
        p.setFirstName("Nina");
        p.setLastName("Simone");
        p.setEmail("nina@example.com");
        p.setPassword("password123");
        personRepository.save(p);

        Activity a = new Activity();
        a.setYear(2024);
        a.setNature(ActivityNature.FORMATION);
        a.setTitle("Formation VueJS");
        a.setPerson(p);
        activityService.create(a);

        List<Activity> results = activityService.searchByTitle("vue");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getTitle()).containsIgnoringCase("VueJS");
    }

    @Test
    void testDeleteActivity() {
        Person p = new Person();
        p.setFirstName("Paul");
        p.setLastName("Dirac");
        p.setEmail("paul@example.com");
        p.setPassword("password123");
        personRepository.save(p);

        Activity a = new Activity();
        a.setYear(2022);
        a.setNature(ActivityNature.AUTRE);
        a.setTitle("Hackathon");
        a.setPerson(p);
        activityService.create(a);

        activityService.delete(a.getId());
        assertThat(activityService.findById(a.getId())).isEmpty();
    }
}
