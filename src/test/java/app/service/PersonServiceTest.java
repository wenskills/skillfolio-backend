package app.service;

import app.dao.PersonRepository;
import app.dao.ResumeRepository;
import app.dto.ActivityDTO;
import app.dto.PersonDTO;
import app.dto.PersonFormDTO;
import app.model.Activity;
import app.model.Person;
import app.model.Resume;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonServiceTest {

    private PersonRepository personRepository;
    private ResumeRepository resumeRepository;
    private PasswordEncoder encoder;
    private ModelMapper mapper;
    private PersonService service;

    @BeforeEach
    void setup() {
        personRepository = mock(PersonRepository.class);
        resumeRepository = mock(ResumeRepository.class);
        encoder = mock(PasswordEncoder.class);
        mapper = new ModelMapper();
        service = new PersonService(personRepository, resumeRepository, encoder, mapper);
    }

    @Test
    void testFindAll_ReturnsPagedDTOs() {
        Person p = new Person();
        p.setId(1L);
        p.setEmail("test@test.com");

        Page<Person> page = new PageImpl<>(List.of(p));
        when(personRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<PersonDTO> result = service.findAll(0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void testFindById_ReturnsDTOWhenExists() {
        Person p = new Person();
        p.setId(1L);
        p.setEmail("email@test.com");

        when(personRepository.findById(1L)).thenReturn(Optional.of(p));

        Optional<PersonDTO> result = service.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("email@test.com");
    }

    @Test
    void testCreate_ThrowsWhenEmailAlreadyExists() {
        PersonFormDTO dto = new PersonFormDTO();
        dto.setEmail("dup@test.com");

        when(personRepository.findByEmailIgnoreCase("dup@test.com"))
                .thenReturn(Optional.of(new Person()));

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("déjà utilisé");
    }

    @Test
    void testCreate_SavesPersonSuccessfully() {
        PersonFormDTO dto = new PersonFormDTO();
        dto.setEmail("new@test.com");
        dto.setPassword("pwd");

        when(personRepository.findByEmailIgnoreCase(dto.getEmail()))
                .thenReturn(Optional.empty());
        when(encoder.encode("pwd")).thenReturn("encodedPwd");

        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            p.setId(99L);
            return p;
        });

        PersonDTO result = service.create(dto);

        assertThat(result.getId()).isEqualTo(99L);
    }

    @Test
    void testUpdate_UpdatesFieldsCorrectly() {
        Person existing = new Person();
        existing.setId(5L);
        existing.setPassword("oldPwd");

        PersonFormDTO dto = new PersonFormDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPassword("newPwd");

        when(personRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(encoder.encode("newPwd")).thenReturn("encodedNew");

        Person saved = new Person();
        saved.setId(5L);
        saved.setFirstName("John");
        saved.setLastName("Doe");
        saved.setPassword("encodedNew");

        when(personRepository.save(any(Person.class))).thenReturn(saved);

        PersonDTO result = service.update(5L, dto);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
    }

    @Test
    void testUpdate_ThrowsWhenNotFound() {
        when(personRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(5L, new PersonFormDTO()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void testDelete_ThrowsWhenNotExists() {
        when(personRepository.existsById(10L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(10L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testDelete_DeletesWhenExists() {
        when(personRepository.existsById(10L)).thenReturn(true);

        service.delete(10L);

        verify(personRepository).deleteById(10L);
    }

    @Test
    void testGetActivitiesByPersonId_ReturnsDTOs() {
        Person p = new Person();
        p.setId(1L);

        Activity a1 = new Activity();
        a1.setId(100L);
        Activity a2 = new Activity();
        a2.setId(200L);

        Resume resume = new Resume();
        resume.setId(10L);
        resume.setOwner(p);
        resume.setActivities(List.of(a1, a2));

        // ✅ Maintenant on récupère via le resume "default"
        when(resumeRepository.findFirstByOwnerIdOrderByIdAsc(1L)).thenReturn(Optional.of(resume));

        List<ActivityDTO> result = service.getActivitiesByPersonId(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getResumeId()).isEqualTo(10L);
    }

    @Test
    void testGetActivitiesByPersonId_ThrowsWhenNotFound() {
        when(resumeRepository.findFirstByOwnerIdOrderByIdAsc(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getActivitiesByPersonId(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
