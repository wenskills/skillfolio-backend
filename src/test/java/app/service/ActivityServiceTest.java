package app.service;

import app.dao.ActivityRepository;
import app.dao.PersonRepository;
import app.dto.ActivityDTO;
import app.model.Activity;
import app.model.Person;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivityServiceTest {

    private ActivityRepository activityRepository;
    private PersonRepository personRepository;
    private ModelMapper mapper;
    private ActivityService service;

    @BeforeEach
    void setup() {
        activityRepository = mock(ActivityRepository.class);
        personRepository = mock(PersonRepository.class);
        mapper = new ModelMapper();
        service = new ActivityService(activityRepository, mapper, personRepository);
    }

    @Test
    void testFindAll_ReturnsMappedDTOs() {
        Activity a = new Activity();
        a.setId(1L);

        when(activityRepository.findAll()).thenReturn(List.of(a));

        List<ActivityDTO> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void testFindById_ReturnsDTOWhenExists() {
        Activity a = new Activity();
        a.setId(1L);

        when(activityRepository.findById(1L)).thenReturn(Optional.of(a));

        Optional<ActivityDTO> result = service.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void testCreate_ThrowsWhenPersonNotFound() {
        ActivityDTO dto = new ActivityDTO();
        dto.setPersonId(50L);

        when(personRepository.findById(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void testCreate_SavesCorrectly() {
        ActivityDTO dto = new ActivityDTO();
        dto.setPersonId(5L);

        Person p = new Person();
        p.setId(5L);

        when(personRepository.findById(5L)).thenReturn(Optional.of(p));

        when(activityRepository.save(any(Activity.class))).thenAnswer(inv -> {
            Activity ac = inv.getArgument(0);
            ac.setId(10L);
            return ac;
        });

        ActivityDTO result = service.create(dto);

        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void testUpdate_ThrowsWhenActivityNotFound() {
        when(activityRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(10L, new ActivityDTO()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void testUpdate_UpdatesPersonWhenChanged() {
        Activity existing = new Activity();
        existing.setId(1L);

        Person oldPerson = new Person();
        oldPerson.setId(2L);

        Person newPerson = new Person();
        newPerson.setId(5L);

        existing.setPerson(oldPerson);

        ActivityDTO dto = new ActivityDTO();
        dto.setPersonId(5L);

        when(activityRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(personRepository.findById(5L)).thenReturn(Optional.of(newPerson));

        when(activityRepository.save(any(Activity.class))).thenAnswer(i -> i.getArgument(0));

        ActivityDTO result = service.update(1L, dto);

        assertThat(result.getPersonId()).isEqualTo(5L);
    }

    @Test
    void testDelete_ThrowsWhenNotFound() {
        when(activityRepository.existsById(10L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(10L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void testDelete_DeletesWhenExists() {
        when(activityRepository.existsById(10L)).thenReturn(true);

        service.delete(10L);

        verify(activityRepository).deleteById(10L);
    }

    @Test
    void testSearchByTitle_ReturnsMappedResults() {
        Activity a = new Activity();
        a.setId(3L);

        when(activityRepository.searchByTitle("java")).thenReturn(List.of(a));

        List<ActivityDTO> result = service.searchByTitle("java");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(3L);
    }
}
