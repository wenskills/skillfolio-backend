package app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**************
 * ENTITE ACTIVITE
 * => titre, année d'activité, type obligatoire
 * => description, site web facultatif
 * ***************/
@Entity
@Table(
        indexes = {
                @Index(name = "idx_activity_title", columnList = "title"),
                @Index(name = "idx_activity_nature", columnList = "nature")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'année est obligatoire")
    private Integer year;

    @NotNull(message = "Le type est obligatoire")
    @Enumerated(EnumType.STRING)
    private ActivityNature nature;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @Column(length = 2000)
    private String description;

    private String webAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    @JsonBackReference
    private Person person;
}
