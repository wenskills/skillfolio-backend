package app.dto;

import app.model.ActivityNature;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDTO {

    private Long id;

    @NotNull
    private Integer year;

    @NotNull
    private ActivityNature nature;

    @NotBlank
    private String title;

    private String description;
    private String webAddress;

    @NotNull
    private Long personId;
}
