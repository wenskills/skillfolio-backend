package app.service;

import app.dao.ResumeRepository;
import app.model.Person;
import app.model.Resume;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ResumeService {

    private final ResumeRepository resumeRepository;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public Resume getOrCreateDefaultResume(Person owner) {
        return resumeRepository.findFirstByOwnerIdOrderByIdAsc(owner.getId())
                .orElseGet(() -> {
                    Resume r = new Resume();
                    r.setOwner(owner);
                    r.setTitle("CV principal");
                    return resumeRepository.save(r);
                });
    }

    @Transactional(readOnly = true)
    public List<Resume> listMine(Person owner) {
        return resumeRepository.findByOwnerId(owner.getId());
    }

    @Transactional(readOnly = true)
    public Resume getMine(Person owner, Long resumeId) {
        Resume r = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found: " + resumeId));
        if (!r.getOwner().getId().equals(owner.getId())) {
            throw new EntityNotFoundException("Resume not found: " + resumeId);
        }
        return r;
    }
}
