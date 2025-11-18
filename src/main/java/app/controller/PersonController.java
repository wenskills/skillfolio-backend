package app.controller;

import app.dto.PersonDTO;
import app.dto.PersonFormDTO;
import app.model.Activity;
import app.model.Person;
import app.service.PersonService;
import jakarta.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    protected final Log logger = LogFactory.getLog(getClass());

    @GetMapping
    public ResponseEntity<Page<Person>> getAllPersons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Person> persons = personService.findAll(page, size);
        return ResponseEntity.ok(persons);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonByid(@PathVariable Long id){
        return personService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@Valid @RequestBody PersonDTO personDTO){

        personDTO.setPassword(passwordEncoder.encode(personDTO.getPassword()));
        ModelMapper modelMapper = new ModelMapper();
        Person person = modelMapper.map(personDTO, Person.class);

        //TODO si on renvoie que certaines informations au front
        Person created = personService.create(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/me")
    public ResponseEntity<Person> updatePerson(
            @Valid @RequestBody PersonFormDTO personDTO) {


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = auth.getName();

        Person existing = personService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        logger.info(personDTO);
        ModelMapper mapper = new ModelMapper();
        Person updatedData = mapper.map(personDTO, Person.class);

        try {
            Person saved = personService.update(existing.getId(), updatedData);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        try {
            personService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @GetMapping("/search")
    public ResponseEntity<Page<Person>> searchPersons(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Person> found = personService.search(keyword, page, size);
        return ResponseEntity.ok(found);
    }


    @GetMapping("/{id}/activities")
    public ResponseEntity<List<Activity>> getActivitiesByPerson(@PathVariable Long id) {
        Optional<Person> personOpt = personService.findById(id);
        if (personOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Person person = personOpt.get();
        List<Activity> cv = person.getCv();

        if (cv == null || cv.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(cv);
    }


}
