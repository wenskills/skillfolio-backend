/*package app.security;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import app.dao.XUserRepository;
import app.model.XUser;

@Service
@Profile("usejwt")
public class JwtUserService {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private XUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String login(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            var user = userRepository.findById(username).get();
            return jwtTokenProvider.createToken(user);
        } catch (AuthenticationException e) {
            throw new JwtException("Invalid username/password supplied");
        }
    }

    public String signup(XUser user) {
        if (userRepository.existsById(user.getUserName())) {
            throw new JwtException("Username is already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return jwtTokenProvider.createToken(user);
    }

    public void delete(String username) {
        userRepository.deleteById(username);
    }

    public Optional<XUser> search(String username) {
        return userRepository.findById(username);
    }

    public String refresh(String username) {
        return jwtTokenProvider.createToken(userRepository.findById(username).get());
    }

    public void invalidateToken(String token) {
        logger.info("Entering the method invalidateToken");
        jwtTokenProvider.invalidateToken(token);
    }

}*/
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

            // Crée un JWT avec le rôle "USER"
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

