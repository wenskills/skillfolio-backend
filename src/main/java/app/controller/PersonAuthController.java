package app.controller;

import app.model.Person;
import app.security.JwtUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/***************
 *  Contrôleur REST responsable de l'authentification
 *  et de la gestion des tokens JWT.
 * ******************/
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Profile("usejwt")
public class PersonAuthController {

    @Autowired
    private JwtUserService jwtPersonService;

    private final ModelMapper modelMapper = new ModelMapper();

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Person person) {
        String token = jwtPersonService.signup(person);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        String token = jwtPersonService.login(email, password);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Person> getCurrentUser(Principal principal) {
        return jwtPersonService.search(principal.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/refresh")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> refreshToken(Principal principal) {
        String newToken = jwtPersonService.refresh(principal.getName());
        return ResponseEntity.ok(newToken);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String header) {
        String token = header.substring("Bearer ".length()).trim();
        jwtPersonService.invalidateToken(token);
        return ResponseEntity.ok().build();
    }
}
