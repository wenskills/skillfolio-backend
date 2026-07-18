package app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/*****************
 * Données envoyées par le front lors du formulaire de modification des informations
 * personnelles de l'utilisateur
 *=> l'utilisateur peut modifier ou pas son mot de passe
 * ********************/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonFormDTO {
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

    @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
    String password;
}
