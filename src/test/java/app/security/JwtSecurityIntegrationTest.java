package app.security;

import app.dao.PersonRepository;
import app.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class JwtSecurityIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private JwtUserService jwtUserService;

    private String token;

    @BeforeEach
    void setup(@Autowired JwtUserService jwtPersonService) {
        personRepository.deleteAll();
        Person person = new Person();
        person.setFirstName("Wendy");
        person.setLastName("Ras");
        person.setEmail("wendy@test.com");
        person.setPassword("password123");

        token = jwtPersonService.signup(person);
        assertThat(token).isNotBlank();
    }

    @Test
    void testPublicGetWithoutToken_ShouldReturn200() throws Exception {
        mvc.perform(get("/api/persons"))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedPostWithoutToken_ShouldReturn401() throws Exception {
        Person person = new Person();
        person.setFirstName("NoAuth");
        person.setLastName("User");
        person.setEmail("noauth@example.com");
        person.setPassword("test123");

        mvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testProtectedPostWithToken_ShouldReturn201() throws Exception {
        Person person = new Person();
        person.setFirstName("Auth");
        person.setLastName("User");
        person.setEmail("auth@example.com");
        person.setPassword("password123");

        mvc.perform(post("/api/persons")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("auth@example.com"));
    }

    @Test
    void testProtectedPostWithInvalidToken_ShouldReturn401() throws Exception {
        Person person = new Person();
        person.setFirstName("Invalid");
        person.setLastName("Token");
        person.setEmail("invalid@example.com");
        person.setPassword("test123");

        mvc.perform(post("/api/persons")
                        .header("Authorization", "Bearer bad.token.example")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRefreshToken_ShouldReturnDifferentToken() {
        String refreshed = jwtUserService.refresh("wendy@test.com");
        assertThat(refreshed).isNotBlank();
        assertThat(refreshed).isNotEqualTo(token);
    }

}
