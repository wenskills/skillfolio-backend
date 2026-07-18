package app.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Data
public class ResumeDetailsDTO {
    private ResumeDTO resume;
    private List<ActivityDTO> activities;
}
