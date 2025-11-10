package app.controller;

import app.dao.ActivityRepository;
import app.dao.PersonRepository;
import app.dto.ActivityDTO;
import app.model.Activity;
import app.model.ActivityNature;
import app.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("open")
class ActivityControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PersonRepository personRepository;

    private ModelMapper modelMapper;

    private Person person;
    private Activity activity1;
    private Activity activity2;

    @BeforeEach
    void setUp() {
        activityRepository.deleteAll();
        personRepository.deleteAll();

        person = new Person();
        person.setFirstName("Alice");
        person.setLastName("Dupont");
        person.setEmail("alice@example.com");
        person.setPassword("password123");
        person.setBirthDate(LocalDate.of(1990, 1, 1));
        personRepository.save(person);

        activity1 = new Activity();
        activity1.setYear(2024);
        activity1.setNature(ActivityNature.PROJET);
        activity1.setTitle("Spring Boot API");
        activity1.setDescription("Développement API CVs");
        activity1.setPerson(person);

        activity2 = new Activity();
        activity2.setYear(2023);
        activity2.setNature(ActivityNature.FORMATION);
        activity2.setTitle("Formation React");
        activity2.setDescription("Front-end moderne");
        activity2.setPerson(person);

        activityRepository.save(activity1);
        activityRepository.save(activity2);
    }

   /* @Test
    void testCreateActivity_Success() throws Exception {
        ActivityDTO dto = new ActivityDTO();
        dto.setYear(2025);
        dto.setNature(ActivityNature.EXPERIENCE);
        dto.setTitle("Stage Dev");
        dto.setDescription("Stage en entreprise");
        dto.setPersonId(person.getId());

        mvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Stage Dev"))
                .andExpect(jsonPath("$.nature").value("EXPERIENCE"))
                .andExpect(jsonPath("$.personId").value(person.getId()));

        assertThat(activityRepository.findAll()).hasSize(3);
    }*/

    @Test
    void testCreateActivity_ShouldFail_BadRequest() throws Exception {
        ActivityDTO invalid = new ActivityDTO();
        invalid.setYear(null);
        invalid.setNature(null);
        invalid.setTitle("");
        invalid.setPersonId(person.getId());

        mvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllActivities() throws Exception {
        mvc.perform(get("/api/activities")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Spring Boot API"))
                .andExpect(jsonPath("$[1].title").value("Formation React"));
    }

    @Test
    void testGetActivityById_Success() throws Exception {
        mvc.perform(get("/api/activities/" + activity1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Boot API"))
                .andExpect(jsonPath("$.nature").value("PROJET"));
    }

    @Test
    void testGetActivityById_NotFound() throws Exception {
        mvc.perform(get("/api/activities/9999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /*@Test
    void testUpdateActivity_Success() throws Exception {
        ActivityDTO updateDTO = new ActivityDTO();
        updateDTO.setYear(2024);
        updateDTO.setNature(ActivityNature.PROJET);
        updateDTO.setTitle("Spring Boot Advanced");
        updateDTO.setDescription("API + Tests avancés");
        updateDTO.setPersonId(person.getId());

        mvc.perform(put("/api/activities/" + activity1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Boot Advanced"))
                .andExpect(jsonPath("$.description").value("API + Tests avancés"));
    }*/

   /* @Test
    void testUpdateActivity_NotFound() throws Exception {
        ActivityDTO dto = new ActivityDTO();
        dto.setYear(2025);
        dto.setNature(ActivityNature.EXPERIENCE);
        dto.setTitle("Nonexistent Update");
        dto.setPersonId(person.getId());

        mvc.perform(put("/api/activities/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }*/

    @Test
    void testUpdateActivity_ShouldFail_BadRequest() throws Exception {
        ActivityDTO invalid = new ActivityDTO();
        invalid.setYear(null);
        invalid.setNature(null);
        invalid.setTitle("");
        invalid.setPersonId(person.getId());

        mvc.perform(put("/api/activities/" + activity1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    void testDeleteActivity_Success() throws Exception {
        mvc.perform(delete("/api/activities/" + activity1.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(activityRepository.findById(activity1.getId())).isEmpty();
    }

    @Test
    void testDeleteActivity_NotFound() throws Exception {
        mvc.perform(delete("/api/activities/9999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /** 🔹 Test GET /api/persons/search?keyword=dup */
    @Test
    void testSearchPersons() throws Exception {
        mvc.perform(get("/api/persons/search?keyword=dup")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[0].lastName").value("Dupont"));
    }

    @Test
    void testSearchPersons_NoResult() throws Exception {
        mvc.perform(get("/api/persons/search?keyword=fantome"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}
