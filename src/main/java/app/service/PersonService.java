package app.service;

import app.dao.PersonRepository;
import app.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    private PasswordEncoder encoder;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    @Transactional(readOnly = true)
    public Page<Person> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return personRepository.findAll(pageable);
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

                    if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
                        existing.setPassword(encoder.encode(updated.getPassword()));
                    }

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
    public Page<Person> search(String keyword, int page, int size) {
        long startDuration = System.currentTimeMillis();
        Pageable pageable = PageRequest.of(page, size);
        Page<Person> nameMatches = personRepository.searchByName(keyword, pageable);
        Page<Person> activityMatches = personRepository.searchByActivity(keyword, pageable);

        Set<Person> merged = new LinkedHashSet<>();
        merged.addAll(nameMatches.getContent());
        merged.addAll(activityMatches.getContent());

        List<Person> mergedList = new ArrayList<>(merged);

        int start = Math.min(page * size, mergedList.size());
        int end = Math.min(start + size, mergedList.size());
        List<Person> pagedList = mergedList.subList(start, end);

        long duration =System.currentTimeMillis() - startDuration;
        System.out.println("Délai de traitement de requete : " + (duration / 1000.0) + " sec.");
        return new PageImpl<>(pagedList, pageable, mergedList.size());
    }

    public Optional<Person> findByEmail(String email) {
        return personRepository.findByEmailIgnoreCase(email);
    }
}
