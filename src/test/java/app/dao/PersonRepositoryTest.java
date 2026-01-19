package app.dao;

import app.model.Activity;
import app.model.ActivityNature;
import app.model.Person;
import app.model.Resume;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("open")
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Test
    void testCreateAndReadPerson() {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setEmail("john.doe@example.com");
        person.setWebsite("https://john.com");
        person.setBirthDate(LocalDate.of(1990, 5, 14));
        person.setPassword("password123");

        Person saved = personRepository.save(person);

        assertThat(saved.getId()).isNotNull();

        Optional<Person> found = personRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testUpdatePerson() {
        Person person = new Person();
        person.setFirstName("Alicile");
        person.setLastName("Smith");
        person.setEmail("alicile@example.com");
        person.setPassword("password123");
        personRepository.save(person);

        person.setLastName("Doe");
        personRepository.save(person);

        Person updated = personRepository.findById(person.getId()).orElseThrow();
        assertThat(updated.getLastName()).isEqualTo("Doe");
    }

    @Test
    void testDeletePerson() {
        Person person = new Person();
        person.setFirstName("Bob");
        person.setLastName("Marley");
        person.setEmail("bobb@example.com");
        person.setPassword("password123");
        personRepository.save(person);

        personRepository.delete(person);

        Optional<Person> deleted = personRepository.findById(person.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void testSearchByName() {
        Person person = new Person();
        person.setFirstName("SearchName");
        person.setLastName("Test");
        person.setEmail("searchname@test.com");
        person.setPassword("password123");
        personRepository.save(person);

        List<Person> page = personRepository.searchByName("search");

        assertThat(page).isNotEmpty();
        assertThat(page.get(0).getFirstName()).containsIgnoringCase("SearchName");
    }

    @Test
    void testSearchByActivity() {
        Person p = new Person();
        p.setFirstName("Act");
        p.setLastName("Person");
        p.setEmail("searchByActivity@test.com");
        p.setPassword("password123");
        personRepository.save(p);

        Resume resume = new Resume();
        resume.setOwner(p);
        resume.setTitle("CV principal");
        resumeRepository.save(resume);

        Activity a = new Activity();
        a.setTitle("SuperActivityTest");
        a.setYear(2024);
        a.setNature(ActivityNature.PROJET);
        a.setResume(resume);
        activityRepository.save(a);

        List<Person> page = personRepository.searchByActivity("super");

        assertThat(page).isNotEmpty();
        assertThat(page.get(0).getId()).isEqualTo(p.getId());
    }

    @Test
    void testFindByEmailIgnoreCase() {
        Person p = new Person();
        p.setFirstName("Mail");
        p.setLastName("Test");
        p.setEmail("mail@test.com");
        p.setPassword("password123");
        personRepository.save(p);

        Optional<Person> found = personRepository.findByEmailIgnoreCase(p.getEmail().toUpperCase());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(p.getId());
    }

    @Test
    void testExistsByEmailIgnoreCase() {
        Person p = new Person();
        p.setFirstName("Check");
        p.setLastName("Email");
        p.setEmail("exists@test.com");
        p.setPassword("password123");
        personRepository.save(p);

        boolean exists = personRepository.existsByEmailIgnoreCase(p.getEmail());

        assertThat(exists).isTrue();
    }
}
