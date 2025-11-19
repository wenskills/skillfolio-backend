package app.controller;

import app.dto.ActivityDTO;
import app.model.Activity;
import app.model.Person;
import app.service.ActivityService;
import app.service.PersonService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private PersonService personService;

    private final ModelMapper mapper;

    public ActivityController(ModelMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getAllActivities() {
        List<ActivityDTO> activities = activityService.findAll();
        if (activities.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long id) {
        return activityService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
/*
    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(@Valid @RequestBody ActivityDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Person person = personService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated person not found"));

        Activity entity = mapper.map(dto, Activity.class);
        entity.setPerson(person);

        ActivityDTO createdDTO = activityService.create(entity);

        // Ajouter personId dans le DTO
        createdDTO.setPersonId(person.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @Valid @RequestBody ActivityDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Activity existing = activityService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        if (!existing.getPerson().getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        modelMapper.map(dto, existing);

        Activity updated = activityService.update(id, existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        if (activityService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        activityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Activity>> searchActivities(@RequestParam String title) {
        List<Activity> results = activityService.searchByTitle(title);
        if (results.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(results);
    }*/
}
