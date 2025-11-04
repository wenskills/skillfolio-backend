package app.dao;

import app.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

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
}