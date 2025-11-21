package app.controller;

import app.dao.PersonRepository;
import app.dto.PersonDTO;
import app.dto.PersonFormDTO;
import app.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("open")
class PersonControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PersonRepository personRepository;

    private Person existingPerson;

    @BeforeEach
    void setup() {
        personRepository.deleteAll();

        existingPerson = new Person();
        existingPerson.setFirstName("Alice");
        existingPerson.setLastName("Wonder");
        existingPerson.setEmail("alice@test.com");
        existingPerson.setPassword("password123");
        existingPerson.setBirthDate(LocalDate.of(1995, 5, 12));

        existingPerson = personRepository.save(existingPerson);
    }
    @BeforeEach
    void mockAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(existingPerson.getEmail(), null, List.of())
        );
    }
    @Test
    void testCreatePerson() throws Exception {

        PersonFormDTO dto = new PersonFormDTO();
        dto.setFirstName("Bob");
        dto.setLastName("Marley");
        dto.setEmail("bob@test.com");
        dto.setPassword("password123");
        dto.setWebsite("https://bob.com");

        mvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("bob@test.com"));

        assertThat(personRepository.findByEmailIgnoreCase("bob@test.com")).isPresent();
    }
    @Test
    void testGetPersonById() throws Exception {
        mvc.perform(get("/api/persons/" + existingPerson.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@test.com"));
    }

    @Test
    void testGetPerson_NotFound() throws Exception {
        mvc.perform(get("/api/persons/99999"))
                .andExpect(status().isNotFound());
    }
    @Test
    void testUpdateProfile() throws Exception {

        PersonFormDTO form = new PersonFormDTO();
        form.setFirstName("AliceUpdated");
        form.setLastName("WonderUpdated");
        form.setEmail("alice@test.com");
        form.setWebsite("https://new-site.com");
        form.setBirthDate(LocalDate.of(1995, 5, 12));
        form.setPassword("NewPassword123");

        mvc.perform(put("/api/persons/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("AliceUpdated"));

        Person updated = personRepository.findByEmailIgnoreCase("alice@test.com").orElseThrow();
        assertThat(updated.getFirstName()).isEqualTo("AliceUpdated");
    }
    @Test
    void testDeletePerson() throws Exception {
        mvc.perform(delete("/api/persons/" + existingPerson.getId()))
                .andExpect(status().isNoContent());

        assertThat(personRepository.findById(existingPerson.getId())).isEmpty();
    }
    @Test
    void testSearchPersons() throws Exception {

        mvc.perform(get("/api/persons/search")
                        .param("keyword", "ali"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("alice@test.com"));
    }

    @Test
    void testSearchPersons_NoResult() throws Exception {
        mvc.perform(get("/api/persons/search")
                        .param("keyword", "noresult"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }
}
