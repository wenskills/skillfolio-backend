package app.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

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

    LocalDate birthDate;

    @NotBlank
    @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
    String password;


}
