package app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/***************
 * Données envoyés par le front lors du formulaire de cooptation
 * ==> nom,prénom, émail obligatoire
 * => si la personne qui coopte connait le website - date d'anniversaire du coopté, elle peut le renseigner.
 * => la personne qui coopte ne renseigne pas le mot de passe, c'est au coopté de modifier ses informations.
 * ******************/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonCreateDTO {

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    private String website;

    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate birthDate;
}
