package app.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/************
 * ENTITE PERSONNE
 * => nom, prenom, email, password obligatoire
 * => site web, date de naissance, cv, token : facultatif
 * **************/
@Entity
@Table(
        indexes = {
                @Index(name = "idx_person_firstname", columnList = "firstName"),
                @Index(name = "idx_person_lastname", columnList = "lastName"),
                @Index(name = "idx_person_email", columnList = "email")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(unique = true, nullable = false)
    private String email;

    private String website;

    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate birthDate;

    @NotBlank
    @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
    private String password;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Activity> cv = new ArrayList<>();

    private String resetToken;
    private LocalDateTime resetTokenExpiration;

}
