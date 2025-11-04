package app.service;

import app.dao.ActivityRepository;
import app.model.Activity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Activity> findById(Long id) {
        return activityRepository.findById(id);
    }

    public Activity create(Activity activity) {
        return activityRepository.save(activity);
    }

    public Activity update(Long id, Activity updated) {
        return activityRepository.findById(id)
                .map(existing -> {
                    existing.setYear(updated.getYear());
                    existing.setNature(updated.getNature());
                    existing.setTitle(updated.getTitle());
                    existing.setDescription(updated.getDescription());
                    existing.setWebAddress(updated.getWebAddress());
                    return activityRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));
    }

    public void delete(Long id) {
        activityRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Activity> searchByTitle(String title) {
        return activityRepository.searchByTitle(title);
    }
}
