package cm.procv.service;

import cm.procv.entity.*;
import cm.procv.repository.CvRepository;
import cm.procv.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CvService {
    private static final int LIMITE_CV_PLAN_FREE = 1;
    private final CvRepository cvRepository;
    private final UtilisateurRepository utilisateurRepository;

    public Cv creer(String email, Cv donnees) {
        Utilisateur utilisateur = utilisateur(email);
        if (utilisateur.getPlan() == Utilisateur.Plan.FREE && cvRepository.countByUtilisateurId(utilisateur.getId()) >= LIMITE_CV_PLAN_FREE) {
            throw new IllegalStateException("Le plan gratuit est limité à 1 CV. Passez au plan Premium pour en créer davantage.");
        }
        donnees.setId(null);
        donnees.setUtilisateur(utilisateur);
        lierEnfants(donnees);
        return cvRepository.save(donnees);
    }

    public List<Cv> listerPourUtilisateur(String email) {
        return cvRepository.findByUtilisateurId(utilisateur(email).getId());
    }

    public Cv obtenirPourUtilisateur(String email, Long id) {
        Cv cv = cvRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("CV introuvable."));
        verifierProprietaire(cv, email);
        return cv;
    }

    public Cv mettreAJour(String email, Long id, Cv donnees) {
        Cv cv = obtenirPourUtilisateur(email, id);
        cv.setTitre(donnees.getTitre());
        cv.setPrenomNom(donnees.getPrenomNom());
        cv.setPosteVise(donnees.getPosteVise());
        cv.setEmail(donnees.getEmail());
        cv.setTelephone(donnees.getTelephone());
        cv.setVille(donnees.getVille());
        cv.setResume(donnees.getResume());
        cv.setModele(donnees.getModele());
        cv.setLangueDocument(donnees.getLangueDocument());
        cv.setDateModification(LocalDateTime.now());
        cv.getExperiences().clear();
        cv.getFormations().clear();
        cv.getCompetences().clear();
        donnees.getExperiences().forEach(e -> { e.setId(null); e.setCv(cv); cv.getExperiences().add(e); });
        donnees.getFormations().forEach(f -> { f.setId(null); f.setCv(cv); cv.getFormations().add(f); });
        donnees.getCompetences().forEach(c -> { c.setId(null); c.setCv(cv); cv.getCompetences().add(c); });
        return cvRepository.save(cv);
    }

    private Utilisateur utilisateur(String email) {
        return utilisateurRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
    }
    private void verifierProprietaire(Cv cv, String email) {
        if (cv.getUtilisateur() == null || !cv.getUtilisateur().getEmail().equalsIgnoreCase(email)) {
            throw new IllegalArgumentException("CV introuvable.");
        }
    }
    private void lierEnfants(Cv cv) {
        cv.getExperiences().forEach(e -> { e.setId(null); e.setCv(cv); });
        cv.getFormations().forEach(f -> { f.setId(null); f.setCv(cv); });
        cv.getCompetences().forEach(c -> { c.setId(null); c.setCv(cv); });
    }
}
