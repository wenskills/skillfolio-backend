package app.controller;

import app.dto.PersonDTO;
import app.model.Activity;
import app.model.Person;
import app.service.PersonService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public ResponseEntity<List<Person>> getAllPersons(){
        List<Person> personList = personService.findAll();
        return ResponseEntity.ok(personList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonByid(@PathVariable Long id){
        return personService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@Valid @RequestBody PersonDTO personDTO){
        ModelMapper modelMapper = new ModelMapper();
        Person person = modelMapper.map(personDTO, Person.class);

        //TODO si on renvoie que certaines informations au front
        Person created = personService.create(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(
            @PathVariable Long id,
            @Valid @RequestBody PersonDTO personDTO) {

        ModelMapper modelMapper = new ModelMapper();
        Person updatedData = modelMapper.map(personDTO,Person.class);

        try {
            Person updated = personService.update(id, updatedData);
            return ResponseEntity.ok(updated);
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
    public ResponseEntity<List<Person>> searchPersons(@RequestParam String keyword){
        List<Person> found = personService.search(keyword);
        if(found.isEmpty()){
            return ResponseEntity.noContent().build();
        }
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
