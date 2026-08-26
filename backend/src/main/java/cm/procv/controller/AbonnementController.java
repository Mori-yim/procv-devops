package cm.procv.controller;

import cm.procv.entity.Utilisateur;
import cm.procv.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Passage au plan Premium. Le paiement (MTN MoMo / Orange Money) est simule
 * ici : en production, cet endpoint declencherait un appel a l'API du
 * fournisseur de paiement puis attendrait une confirmation (webhook) avant
 * de mettre le plan a jour.
 */
@RestController
@RequestMapping("/api/abonnement")
@RequiredArgsConstructor
public class AbonnementController {

    private final UtilisateurRepository utilisateurRepository;

    @PostMapping("/souscrire")
    public Map<String, String> souscrire(Authentication auth, @RequestBody Map<String, String> requete) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        utilisateur.setPlan(Utilisateur.Plan.PREMIUM);
        utilisateurRepository.save(utilisateur);

        return Map.of(
            "statut", "CONFIRME",
            "moyenPaiement", requete.getOrDefault("moyenPaiement", "MTN_MOMO"),
            "plan", "PREMIUM"
        );
    }
}
