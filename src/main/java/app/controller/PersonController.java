package app.controller;

import app.dto.ActivityDTO;
import app.dto.PersonDTO;
import app.dto.PersonFormDTO;
import app.model.Activity;
import app.model.Person;
import app.service.PersonService;
import jakarta.persistence.EntityNotFoundException;
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


    @GetMapping
    public ResponseEntity<Page<PersonDTO>> getAllPersons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(personService.findAll(page, size));
    }


    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable Long id) {
        return personService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PersonDTO> createPerson(@Valid @RequestBody PersonDTO personDTO) {
        PersonDTO created = personService.create(personDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/me")
    public ResponseEntity<PersonDTO> updatePerson(
            @Valid @RequestBody PersonFormDTO personDTO) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = auth.getName();

        return personService.findByEmail(email)
                .map(existingPerson -> {
                    PersonDTO updated = personService.update(existingPerson.getId(), personDTO);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
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
    public ResponseEntity<Page<PersonDTO>> searchPersons(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(personService.search(keyword, page, size));
    }


    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByPerson(@PathVariable Long id) {
        try {
            List<ActivityDTO> activities = personService.getActivitiesByPersonId(id);
            return ResponseEntity.ok(activities);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
