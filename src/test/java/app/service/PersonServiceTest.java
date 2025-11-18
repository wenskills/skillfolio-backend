package app.service;

import app.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("open")
class PersonServiceTest {

    @Autowired
    private PersonService personService;

    @Test
    void testCreateAndFindPerson() {
        Person p = new Person();
        p.setFirstName("Alice");
        p.setLastName("Dupont");
        p.setEmail("alice@example.com");
        p.setWebsite("https://alice.dev");
        p.setBirthDate(LocalDate.of(1995, 4, 15));
        p.setPassword("password123");

        Person saved = personService.create(p);
        assertThat(saved.getId()).isNotNull();

        var found = personService.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(saved.getEmail());
    }

    @Test
    void testUpdatePerson() {
        Person p = new Person();
        p.setFirstName("Bob");
        p.setLastName("Marley");
        p.setEmail("bob@example.com");
        p.setPassword("password123");

        Person saved = personService.create(p);

        // Mise à jour
        saved.setLastName("Marlo");
        Person updated = personService.update(saved.getId(), saved);

        assertThat(updated.getLastName()).isEqualTo("Marlo");
    }

    @Test
    void testSearchPerson() {
        Person p = new Person();
        p.setFirstName("Charlie");
        p.setLastName("Brown");
        p.setEmail("charlie@example.com");
        p.setPassword("password123");
        personService.create(p);

        Page<Person> results = personService.search("char", 0, 10);

        assertThat(results.getContent()).isNotEmpty();
        assertThat(results.getContent().stream()
                .anyMatch(per -> per.getFirstName().equalsIgnoreCase("Charlie"))
        ).isTrue();
    }

    @Test
    void testDeletePerson() {
        Person p = new Person();
        p.setFirstName("David");
        p.setLastName("Jones");
        p.setEmail("david@example.com");
        p.setPassword("password123");

        Person saved = personService.create(p);
        Long id = saved.getId();

        personService.delete(id);
        assertThat(personService.findById(id)).isEmpty();
    }
}
