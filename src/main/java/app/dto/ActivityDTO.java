package app.dto;

import app.model.ActivityNature;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**********
 *  Données activity envoyés au front
 * **************/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActivityDTO {

    private Long id;

    @NotNull(message = "L'année est obligatoire")
    private Integer year;

    @NotNull(message = "Le type est obligatoire")
    private ActivityNature nature;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    private String description;
    private String webAddress;

    @NotNull
    private Long personId;
}
