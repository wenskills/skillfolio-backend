package app.service;

import app.dao.PersonRepository;
import app.dto.PersonCreateDTO;
import app.dto.ResetPasswordDTO;
import app.model.Person;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    @Transactional(readOnly = true)
    public Page<Person> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return personRepository.findAll(pageable);
    }
    @Transactional(readOnly = true)
    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }
    public Person create(Person person) {
        return personRepository.save(person);
    }

    public Person update(Long id, Person updated) {
        return personRepository.findById(id)
                .map(existing -> {

                    existing.setFirstName(updated.getFirstName());
                    existing.setLastName(updated.getLastName());
                    existing.setEmail(updated.getEmail());
                    existing.setWebsite(updated.getWebsite());
                    existing.setBirthDate(updated.getBirthDate());

                    if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
                        existing.setPassword(encoder.encode(updated.getPassword()));
                    }

                    return personRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));
    }
    public void delete(Long id) {
        if (!personRepository.existsById(id)) {
            throw new IllegalArgumentException("Person not found with id " + id);
        }
        personRepository.deleteById(id);
    }
    @Transactional(readOnly = true)
    public Page<Person> search(String keyword, int page, int size) {
        long startDuration = System.currentTimeMillis();
        Pageable pageable = PageRequest.of(page, size);
        Page<Person> nameMatches = personRepository.searchByName(keyword, pageable);
        Page<Person> activityMatches = personRepository.searchByActivity(keyword, pageable);

        Set<Person> merged = new LinkedHashSet<>();
        merged.addAll(nameMatches.getContent());
        merged.addAll(activityMatches.getContent());

        List<Person> mergedList = new ArrayList<>(merged);

        int start = Math.min(page * size, mergedList.size());
        int end = Math.min(start + size, mergedList.size());
        List<Person> pagedList = mergedList.subList(start, end);

        long duration =System.currentTimeMillis() - startDuration;
        System.out.println("Délai de traitement de requete : " + (duration / 1000.0) + " sec.");
        return new PageImpl<>(pagedList, pageable, mergedList.size());
    }

    public Optional<Person> findByEmail(String email) {
        return personRepository.findByEmailIgnoreCase(email);
    }

    /**
     * Création via cooptation : mot de passe + token envoyés par mail
     */
    public Person createViaCooptation(PersonCreateDTO dto) {

        if (personRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        Person p = new Person();
        p.setFirstName(dto.getFirstName());
        p.setLastName(dto.getLastName());
        p.setEmail(dto.getEmail());
        p.setBirthDate(dto.getBirthDate());
        p.setWebsite(dto.getWebsite());

        // mot de passe temporaire
        String tempPass = RandomStringUtils.randomAlphanumeric(10);
        p.setPassword(encoder.encode(tempPass));

        // token de reset
        String token = UUID.randomUUID().toString();
        p.setResetToken(token);
        p.setResetTokenExpiration(LocalDateTime.now().plusHours(24));

        Person saved = personRepository.save(p);

        // email
        emailService.sendWelcomeEmail(saved, tempPass, token);

        return saved;
    }

    /**
     * reset-password
     */
    public boolean resetPassword(ResetPasswordDTO dto) {

        Optional<Person> opt = personRepository.findByResetToken(dto.getToken());
        if (opt.isEmpty()) return false;

        Person user = opt.get();

        if (user.getResetTokenExpiration() == null ||
                user.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
        }

        user.setPassword(encoder.encode(dto.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiration(null);

        personRepository.save(user);
        return true;
    }
}
