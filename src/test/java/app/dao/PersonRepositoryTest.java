package app.dao;

import app.model.Activity;
import app.model.ActivityNature;
import app.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

        var page = personRepository.searchByName("search", PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getFirstName()).containsIgnoringCase("SearchName");
    }

    @Test
    void testSearchByActivity() {

        Person p = new Person();
        p.setFirstName("Act");
        p.setLastName("Person");
        p.setEmail("searchByActivity@test.com");
        p.setPassword("password123");
        personRepository.save(p);

        Activity a = new Activity();
        a.setTitle("SuperActivityTest");
        a.setYear(2024);
        a.setNature(ActivityNature.PROJET);
        a.setPerson(p);
        activityRepository.save(a);

        var page = personRepository.searchByActivity("super", PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getId()).isEqualTo(p.getId());
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