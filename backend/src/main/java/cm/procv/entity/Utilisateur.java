package cm.procv.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomComplet;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    private String telephone;

    @Enumerated(EnumType.STRING)
    private Plan plan = Plan.FREE;

    private LocalDateTime dateCreation = LocalDateTime.now();

    public enum Plan {
        FREE,     // 1 CV maximum, filigrane sur le PDF
        PREMIUM   // CV illimites, sans filigrane
    }
}
