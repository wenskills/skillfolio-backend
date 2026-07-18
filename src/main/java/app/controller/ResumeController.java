package app.controller;

import app.dao.PersonRepository;
import app.dao.ResumeRepository;
import app.dto.ActivityDTO;
import app.dto.ResumeDTO;
import app.dto.ResumeDetailsDTO;
import app.model.Person;
import app.model.Resume;
import app.service.ResumeService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final ResumeRepository resumeRepository;
    private final PersonRepository personRepository;
    private final ModelMapper mapper;

    public ResumeController(
            ResumeService resumeService,
            ResumeRepository resumeRepository,
            PersonRepository personRepository,
            ModelMapper mapper
    ) {
        this.resumeService = resumeService;
        this.resumeRepository = resumeRepository;
        this.personRepository = personRepository;
        this.mapper = mapper;
    }

    @GetMapping("/me/default")
    public ResponseEntity<ResumeDetailsDTO> getMyDefaultResume() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Person me = personRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + email));

        Resume resume = resumeService.getOrCreateDefaultResume(me);

        return ResponseEntity.ok(toDetailsDto(resume));
    }

    @GetMapping("/by-person/{personId}/default")
    public ResponseEntity<ResumeDetailsDTO> getDefaultByPerson(@PathVariable Long personId) {
        Resume resume = resumeRepository.findFirstByOwnerIdOrderByIdAsc(personId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun CV pour la personne " + personId));

        return ResponseEntity.ok(toDetailsDto(resume));
    }

    private ResumeDetailsDTO toDetailsDto(Resume resume) {
        ResumeDetailsDTO out = new ResumeDetailsDTO();
        out.setResume(mapper.map(resume, ResumeDTO.class));

        var acts = resume.getActivities().stream()
                .sorted((a, b) -> {
                    Integer ya = a.getYear();
                    Integer yb = b.getYear();
                    if (ya == null && yb == null) return 0;
                    if (ya == null) return 1;
                    if (yb == null) return -1;
                    return Integer.compare(yb, ya);
                })
                .map(a -> {
                    ActivityDTO dto = mapper.map(a, ActivityDTO.class);
                    dto.setResumeId(resume.getId());
                    return dto;
                })
                .toList();

        out.setActivities(acts);
        return out;
    }

}
