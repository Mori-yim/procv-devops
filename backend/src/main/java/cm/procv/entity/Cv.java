package cm.procv.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "cvs")
public class Cv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @JsonIgnore
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private String titre; // ex: "Candidature Developpeur Java"

    private String prenomNom;
    private String posteVise;
    private String email;
    private String telephone;
    private String ville;

    @Column(length = 2000)
    private String resume; // profil / accroche

    @Enumerated(EnumType.STRING)
    private Modele modele = Modele.CLASSIQUE;

    @Enumerated(EnumType.STRING)
    private Langue langueDocument = Langue.FR;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Formation> formations = new ArrayList<>();

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Competence> competences = new ArrayList<>();

    private LocalDateTime dateCreation = LocalDateTime.now();
    private LocalDateTime dateModification = LocalDateTime.now();

    public enum Modele {
        CLASSIQUE, MODERNE, MINIMALISTE
    }

    public enum Langue {
        FR, EN
    }
}
