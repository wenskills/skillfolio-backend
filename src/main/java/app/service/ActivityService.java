package app.service;

import app.dao.ActivityRepository;
import app.dto.ActivityDTO;
import app.model.Activity;
import app.model.Person;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ModelMapper mapper;

    public ActivityService(ActivityRepository activityRepository,ModelMapper mapper) {
        this.activityRepository = activityRepository;
        this.mapper = mapper;
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
        Activity entity = mapper.map(dto, Activity.class);
        Activity saved = activityRepository.save(entity);
        return mapper.map(saved, ActivityDTO.class);
    }

    public ActivityDTO update(Long id, ActivityDTO updated) {
        Activity existing = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        mapper.map(updated, existing); // copie les champs du DTO vers l'entité existante

        Activity saved = activityRepository.save(existing);
        return mapper.map(saved, ActivityDTO.class);
    }

    public void delete(Long id) {
        activityRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> searchByTitle(String title) {
        return activityRepository.searchByTitle(title)
                .stream()
                .map(a -> mapper.map(a, ActivityDTO.class))
                .toList();}


}
