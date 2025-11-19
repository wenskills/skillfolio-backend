package app.service;

import app.dao.PersonRepository;
import app.dto.ActivityDTO;
import app.dto.PersonDTO;
import app.dto.PersonFormDTO;
import app.model.Activity;
import app.model.Person;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.stream.Stream;

@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;

    private final PasswordEncoder encoder;

    private final ModelMapper mapper;

    public PersonService(PersonRepository personRepository,
                         PasswordEncoder encoder,
                         ModelMapper mapper) {
        this.personRepository = personRepository;
        this.encoder = encoder;
        this.mapper = mapper;
    }
    @Transactional(readOnly = true)
    public Page<PersonDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return personRepository.findAll(pageable)
                .map(person -> mapper.map(person, PersonDTO.class));
    }

    @Transactional(readOnly = true)
    public Optional<PersonDTO> findById(Long id) {
        return personRepository.findById(id)
                .map(person -> mapper.map(person, PersonDTO.class));
    }

    public PersonDTO create(PersonDTO dto) {
        if (personRepository.findByEmailIgnoreCase(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }
        Person entity = mapper.map(dto, Person.class);
        entity.setPassword(encoder.encode(dto.getPassword()));
        Person saved = personRepository.save(entity);
        return mapper.map(saved, PersonDTO.class);
    }

    public PersonDTO update(Long id, PersonFormDTO updatedDto) {
        Person existing = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id " + id));

        existing.setFirstName(updatedDto.getFirstName());
        existing.setLastName(updatedDto.getLastName());
        existing.setEmail(updatedDto.getEmail());
        existing.setWebsite(updatedDto.getWebsite());
        existing.setBirthDate(updatedDto.getBirthDate());

        if (updatedDto.getPassword() != null && !updatedDto.getPassword().isBlank()) {
            existing.setPassword(encoder.encode(updatedDto.getPassword()));
        }

        Person saved = personRepository.save(existing);
        return mapper.map(saved, PersonDTO.class);
    }

    public void delete(Long id) {
        if (!personRepository.existsById(id)) {
            throw new IllegalArgumentException("Person not found with id " + id);
        }
        personRepository.deleteById(id);
    }
    @Transactional(readOnly = true)
    public Page<PersonDTO> search(String keyword, int page, int size) {
        long startDuration = System.currentTimeMillis();
        Pageable pageable = PageRequest.of(page, size);
        Page<Person> results = personRepository.searchGlobal(keyword, pageable);
        long duration =System.currentTimeMillis() - startDuration;
        System.out.println("Délai de traitement de requete : " + (duration / 1000.0) + " sec.");
        return results.map(p -> mapper.map(p, PersonDTO.class));    }

    public Optional<PersonDTO> findByEmail(String email) {
        return personRepository.findByEmailIgnoreCase(email)
                .map(person -> mapper.map(person, PersonDTO.class));
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> getActivitiesByPersonId(Long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new EntityNotFoundException("Personne introuvable avec l'id " + personId));

        return person.getCv().stream()
                .map(activity -> {
                    ActivityDTO dto = mapper.map(activity, ActivityDTO.class);
                    dto.setPersonId(person.getId());
                    return dto;
                })
                .toList();
    }
}
