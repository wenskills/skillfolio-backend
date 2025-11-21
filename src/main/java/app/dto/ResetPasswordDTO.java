package app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**************
 * Données pour la réinitialisation du mot de passe
 * *******************/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDTO {

    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
    private String newPassword;

    @NotBlank
    @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
    private String confirmPassword;
}

