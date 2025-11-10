package app.controller;

import app.dao.ActivityRepository;
import app.dao.PersonRepository;
import app.dto.PersonDTO;
import app.model.Activity;
import app.model.ActivityNature;
import app.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.modelmapper.ModelMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("open")
class PersonControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Person person1;
    private Person person2;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();

        person1 = new Person();
        person1.setFirstName("Alice");
        person1.setLastName("Dupont");
        person1.setEmail("alice@example.com");
        person1.setPassword("password123");
        person1.setBirthDate(LocalDate.of(1990, 1, 1));

        person2 = new Person();
        person2.setFirstName("Bob");
        person2.setLastName("Marley");
        person2.setEmail("bob@example.com");
        person2.setPassword("password123");
        person2.setBirthDate(LocalDate.of(1985, 5, 5));

        personRepository.save(person1);
        personRepository.save(person2);
    }

    @Test
    void testGetAllPersons() throws Exception {
        mvc.perform(get("/api/persons")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].firstName").value("Bob"));
    }

    @Test
    void testGetPersonById() throws Exception {
        mvc.perform(get("/api/persons/" + person1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void testGetPersonById_NotFound() throws Exception {
        mvc.perform(get("/api/persons/9999")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    void testCreatePerson_Success() throws Exception {
        Person personToSend = new Person();
        personToSend.setFirstName("Wendy");
        personToSend.setLastName("Ras");
        personToSend.setEmail("wendy@ras.com");
        personToSend.setPassword("12356789");
        personToSend.setBirthDate(LocalDate.of(2002, 3, 13));

        ModelMapper modelMapper = new ModelMapper();

        PersonDTO personDTO = modelMapper.map(personToSend, PersonDTO.class);

        mvc.perform(post("/api/persons").contentType(String.valueOf(org.junit.jupiter.api.extension.MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(personDTO))).andExpect(status().isCreated())
                    .andExpect(jsonPath("$.firstName").value("Wendy"))
                    .andExpect(jsonPath("$.lastName").value("Ras"))
                    .andExpect(jsonPath("$.email").value("wendy@ras.com"))
                    .andExpect(jsonPath("$.id").exists());

    }

    @Test
    void testCreatePerson_ShouldFail_BadRequest() throws Exception {

        Person invalidPerson = new Person();
        invalidPerson.setFirstName("");
        invalidPerson.setLastName("Ras");
        invalidPerson.setEmail("invalid-email");
        invalidPerson.setPassword("123");
        invalidPerson.setBirthDate(LocalDate.of(2002, 3, 13));

        ModelMapper modelMapper = new ModelMapper();
        PersonDTO invalidDTO = modelMapper.map(invalidPerson, PersonDTO.class);

        mvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest()); // 400
    }

    @Test
    void testUpdatePerson_Success() throws Exception {

        String json = """
        {
          "firstName": "Alice",
          "lastName": "Martin",
          "email": "alice@example.com",
          "password": "password123",
          "website": "https://alice.dev"
        }
        """;

        mvc.perform(put("/api/persons/" + person1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Martin"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void testUpdatePerson_NotFound() throws Exception {
        String json = """
        {
          "firstName": "Ghost",
          "lastName": "User",
          "email": "ghost@example.com",
          "password": "password123"
        }
        """;

        mvc.perform(put("/api/persons/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeletePerson_Success() throws Exception {

        assertThat(personRepository.findById(person1.getId())).isPresent();

        mvc.perform(delete("/api/persons/" + person1.getId()))
                .andDo(print())
                .andExpect(status().isNoContent()); // 204 attendu

        assertThat(personRepository.findById(person1.getId())).isEmpty();
    }

    @Test
    void testDeletePerson_ShouldFail() throws Exception {

        mvc.perform(delete("/api/persons/" + 999))
                .andDo(print())
                .andExpect(status().isNotFound()); // 204 attendu


    }

    @Test
    void testSearchPersons_LastName() throws Exception {
        mvc.perform(get("/api/persons/search?keyword=dup")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[0].lastName").value("Dupont"));
    }

    @Test
    void testSearchPersons_FirstName() throws Exception {
        mvc.perform(get("/api/persons/search?keyword=li")
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

    @Test
    void testGetActivitiesByPerson_Success() throws Exception {
        Activity  activity1 = new Activity();
        activity1.setYear(2025);
        activity1.setNature(ActivityNature.PROJET);
        activity1.setTitle("Archictecture des apps");
        activity1.setDescription("Développement API CVs");
        activity1.setPerson(person1);
        activityRepository.save(activity1);
        mvc.perform(get("/api/persons/" + person1.getId() + "/activities")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testGetActivitiesByPerson_NotFound() throws Exception {
        mvc.perform(get("/api/persons/9999/activities"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


}
