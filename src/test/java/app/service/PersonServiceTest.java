package app.service;

import app.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PersonServiceTest {

    @Autowired
    private PersonService personService;

    @Test
    void testCreateAndFindPerson() {
        Person p = new Person();
        p.setFirstName("Alice");
        p.setLastName("Dupont");
        p.setEmail("alice.dupont@example.com");
        p.setWebsite("https://alice.dev");
        p.setBirthDate(LocalDate.of(1995, 4, 15));
        p.setPassword("password123");

        Person saved = personService.create(p);
        assertThat(saved.getId()).isNotNull();

        var found = personService.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("alice.dupont@example.com");
    }

    @Test
    void testUpdatePerson() {
        Person p = new Person();
        p.setFirstName("Bob");
        p.setLastName("Marley");
        p.setEmail("bob.marley@example.com");
        p.setPassword("password123");
        personService.create(p);

        p.setLastName("Marlo");
        Person updated = personService.update(p.getId(), p);

        assertThat(updated.getLastName()).isEqualTo("Marlo");
    }

    @Test
    void testSearchPerson() {
        Person p = new Person();
        p.setFirstName("Charlie");
        p.setLastName("Brown");
        p.setEmail("charlie.brown@example.com");
        p.setPassword("password123");
        personService.create(p);

        List<Person> results = personService.search("char");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getFirstName()).containsIgnoringCase("Charlie");
    }

    @Test
    void testDeletePerson() {
        Person p = new Person();
        p.setFirstName("David");
        p.setLastName("Jones");
        p.setEmail("david.jones@example.com");
        p.setPassword("password123");
        personService.create(p);

        personService.delete(p.getId());
        assertThat(personService.findById(p.getId())).isEmpty();
    }
}
