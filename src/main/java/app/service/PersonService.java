package app.service;

import app.dao.PersonRepository;
import app.model.Person;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return personRepository.findAll();
    }
    @Transactional(readOnly = true)
    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }
    public Person create(Person person) {
        return personRepository.save(person);
    }

    public Person update(Long id, Person updated) {
        return personRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(updated.getFirstName());
                    existing.setLastName(updated.getLastName());
                    existing.setEmail(updated.getEmail());
                    existing.setWebsite(updated.getWebsite());
                    existing.setBirthDate(updated.getBirthDate());
                    existing.setPassword(updated.getPassword());
                    return personRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));
    }
    public void delete(Long id) {
        if (!personRepository.existsById(id)) {
            throw new IllegalArgumentException("Person not found with id " + id);
        }
        personRepository.deleteById(id);
    }
    @Transactional(readOnly = true)
    public List<Person> search(String keyword) {
        return personRepository.searchByKeyword(keyword);
    }

}
