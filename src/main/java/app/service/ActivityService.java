package app.service;

import app.dao.ActivityRepository;
import app.dao.PersonRepository;
import app.dao.ResumeRepository;
import app.dto.ActivityDTO;
import app.model.Activity;
import app.model.Person;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**************
 *  GESTION METIER DES ACTIVITES
 * ****************/
@Service
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ResumeRepository resumeRepository;
    private final ModelMapper mapper;

    public ActivityService(ActivityRepository activityRepository, ModelMapper mapper, ResumeRepository resumeRepository) {
        this.activityRepository = activityRepository;
        this.mapper = mapper;
        this.resumeRepository = resumeRepository;
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> findAll() {
        return activityRepository.findAll()
                .stream()
                .map(a -> mapper.map(a, ActivityDTO.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ActivityDTO> findById(Long id) {
        return activityRepository.findById(id)
                .map(a -> mapper.map(a, ActivityDTO.class));
    }

    public ActivityDTO create(ActivityDTO dto) {
        var resume = resumeRepository.findById(dto.getResumeId())
                .orElseThrow(() -> new EntityNotFoundException("Resume not found with id: " + dto.getResumeId()));

        Activity entity = mapper.map(dto, Activity.class);
        entity.setResume(resume);
        entity.setId(null);

        Activity saved = activityRepository.save(entity);

        ActivityDTO out = mapper.map(saved, ActivityDTO.class);
        out.setResumeId(resume.getId());
        return out;
    }
    public ActivityDTO update(Long id, ActivityDTO dto) {
        Activity existing = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found with id: " + id));

        if (dto.getResumeId() != null && !dto.getResumeId().equals(existing.getResume().getId())) {
            var newResume = resumeRepository.findById(dto.getResumeId())
                    .orElseThrow(() -> new EntityNotFoundException("Resume not found with id: " + dto.getResumeId()));
            existing.setResume(newResume);
        }

        mapper.map(dto, existing);
        existing.setId(id);

        Activity saved = activityRepository.save(existing);

        ActivityDTO out = mapper.map(saved, ActivityDTO.class);
        out.setResumeId(saved.getResume().getId());
        return out;
    }

    public void delete(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new EntityNotFoundException("Activity not found with id: " + id);
        }
        activityRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> searchByTitle(String title) {
        return activityRepository.searchByTitle(title)
                .stream()
                .map(a -> mapper.map(a, ActivityDTO.class))
                .toList();}


}
