package cm.procv.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "experiences")
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cv_id", nullable = false)
    @JsonIgnore
    private Cv cv;

    private String poste;
    private String entreprise;
    private String ville;
    private String periode; // ex: "Jan 2023 - Present"

    @Column(length = 1000)
    private String description;
}
