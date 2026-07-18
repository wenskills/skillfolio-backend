package app.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Data
public class ResumeDTO {
    private Long id;
    private String title;
    private String template;
}
