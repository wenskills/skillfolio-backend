
package app.security;

import app.dao.PersonRepository;
import app.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/*************Gestion d'authentification utilisateur****************/
@Service
@Profile("usejwt")
public class JwtUserService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String login(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            Person person = personRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new JwtException("Person not found"));

            return jwtTokenProvider.createToken(email, "USER");

        } catch (AuthenticationException e) {
            throw new JwtException("Invalid email/password supplied");
        }
    }


    public String signup(Person person) {
        if (personRepository.existsByEmailIgnoreCase(person.getEmail())) {
            throw new JwtException("Email is already in use");
        }

        person.setPassword(passwordEncoder.encode(person.getPassword()));
        personRepository.save(person);

        return jwtTokenProvider.createToken(person.getEmail(), "USER");
    }

    public Optional<Person> search(String email) {
        return personRepository.findByEmailIgnoreCase(email);
    }


    public void delete(String email) {
        personRepository.findByEmailIgnoreCase(email)
                .ifPresentOrElse(
                        personRepository::delete,
                        () -> { throw new JwtException("Person not found"); }
                );
    }


    public String refresh(String email) {
        var person = personRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new JwtException("Person not found"));
        return jwtTokenProvider.createToken(person.getEmail(), "USER");
    }

    public void invalidateToken(String token) {
        jwtTokenProvider.invalidateToken(token);
    }
}

