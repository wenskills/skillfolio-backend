package app.controller;

import app.dao.PersonRepository;
import app.model.Person;
import app.security.JwtUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("usejwt")
class PersonAuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonRepository personRepository;

    @MockitoBean
    private JwtUserService jwtUserService;

    private Person person;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();

        person = new Person();
        person.setFirstName("Alice");
        person.setLastName("Dupont");
        person.setEmail("alice@example.com");
        person.setPassword("password123");
        person.setBirthDate(LocalDate.of(1990, 1, 1));
        personRepository.save(person);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(person.getEmail(), null, List.of())
        );
    }

    @Test
    void testSignup_Success() throws Exception {
        Mockito.when(jwtUserService.signup(any(Person.class)))
                .thenReturn("TOKEN123");

        Person p = new Person();
        p.setFirstName("Bob");
        p.setLastName("Martin");
        p.setEmail("bob@example.com");
        p.setPassword("passwordABC");
        p.setBirthDate(LocalDate.of(1999, 3, 3));

        mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("TOKEN123"));
    }

    @Test
    void testLogin_Success() throws Exception {
        Mockito.when(jwtUserService.login("alice@example.com", "password123"))
                .thenReturn("LOGINTOKEN");

        mvc.perform(post("/auth/login")
                        .param("email", "alice@example.com")
                        .param("password", "password123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("LOGINTOKEN"));
    }

    @Test
    void testGetCurrentUser_Success() throws Exception {
        Mockito.when(jwtUserService.search(eq(person.getEmail())))
                .thenReturn(Optional.of(person));

        mvc.perform(get("/auth/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void testGetCurrentUser_NotFound() throws Exception {
        Mockito.when(jwtUserService.search("ghost@test.com"))
                .thenReturn(Optional.empty());

        mvc.perform(get("/auth/me").principal(() -> "ghost@test.com"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        Mockito.when(jwtUserService.refresh(person.getEmail()))
                .thenReturn("NEW_TOKEN_ABC");

        mvc.perform(get("/auth/refresh"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("NEW_TOKEN_ABC"));
    }

}
