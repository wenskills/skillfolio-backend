package app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Table(indexes = {
        @Index(name = "idx_resume_owner", columnList = "owner_id"),
        @Index(name = "idx_resume_title", columnList = "title")
})
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonBackReference(value = "person-resumes")
    private Person owner;

    @Column(nullable = false)
    private String title = "CV principal";

    // optionnel pour plus tard
    private String template = "default";

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // si Resume -> activities existe, on gère aussi cette boucle :
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "resume-activities")
    private List<Activity> activities = new ArrayList<>();

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
