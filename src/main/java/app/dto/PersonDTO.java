package app.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {

    @NotBlank
    String lastName;

    @NotBlank
    String firstName;

    @NotBlank
    @Email
    private String email;

    String website;

    LocalDate birthDate;

    @NotBlank
    @Size(min = 8)
    String password;
}
