package app.service;

import app.dao.ActivityRepository;
import app.dao.PersonRepository;
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
    private final PersonRepository personRepository;
    private final ModelMapper mapper;

    public ActivityService(ActivityRepository activityRepository,ModelMapper mapper, PersonRepository personRepository) {
        this.activityRepository = activityRepository;
        this.mapper = mapper;
        this.personRepository=personRepository ;
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
        Person person = personRepository.findById(dto.getPersonId())
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + dto.getPersonId()));

        Activity entity = mapper.map(dto, Activity.class);

        entity.setPerson(person);
        entity.setId(null);

        Activity saved = activityRepository.save(entity);
        return mapper.map(saved, ActivityDTO.class);
    }

    public ActivityDTO update(Long id, ActivityDTO dto) {
        Activity existingActivity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found with id: " + id));

        if (dto.getPersonId() != null && !dto.getPersonId().equals(existingActivity.getPerson().getId())) {
            Person newPerson = personRepository.findById(dto.getPersonId())
                    .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + dto.getPersonId()));
            existingActivity.setPerson(newPerson);
        }

        mapper.map(dto, existingActivity);

        existingActivity.setId(id);

        Activity saved = activityRepository.save(existingActivity);
        return mapper.map(saved, ActivityDTO.class);
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
