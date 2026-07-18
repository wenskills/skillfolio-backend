package app.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/*****************
 * Données envoyées au front pour une personne
 * => pas de mot de passe : donnée sensible!
 * *********************/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonDTO {
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    String lastName;

    @NotBlank(message = "Le prénom est obligatoire")
    String firstName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    String website;

    @Past(message = "La date de naissance doit être dans le passé")
    LocalDate birthDate;

    List<ActivityDTO> cv;
}
