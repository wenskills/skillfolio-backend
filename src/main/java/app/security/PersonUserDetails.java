package app.security;

import app.dao.PersonRepository;
import app.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("usejwt")
public class PersonUserDetails implements UserDetailsService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Person p = personRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Person not found with email: " + email));

        /* Par défaut, toutes les Person ont le rôle USER*/
        var authorities = List.of(new SimpleGrantedAuthority("USER"));

        return User
                .withUsername(p.getEmail())
                .password(p.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
