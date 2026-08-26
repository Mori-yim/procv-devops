package cm.procv.service;

import cm.procv.dto.AuthResponse;
import cm.procv.dto.ConnexionRequest;
import cm.procv.dto.InscriptionRequest;
import cm.procv.entity.Utilisateur;
import cm.procv.repository.UtilisateurRepository;
import cm.procv.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse inscrire(InscriptionRequest requete) {
        if (utilisateurRepository.existsByEmail(requete.getEmail())) {
            throw new IllegalArgumentException("Un compte existe deja avec cet email.");
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNomComplet(requete.getNomComplet());
        utilisateur.setEmail(requete.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(requete.getMotDePasse()));
        utilisateur.setTelephone(requete.getTelephone());
        utilisateurRepository.save(utilisateur);

        String token = jwtUtil.genererToken(utilisateur.getEmail());
        return new AuthResponse(token, utilisateur.getNomComplet(), utilisateur.getEmail(), utilisateur.getPlan().name());
    }

    public AuthResponse connecter(ConnexionRequest requete) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(requete.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect."));
        if (!passwordEncoder.matches(requete.getMotDePasse(), utilisateur.getMotDePasse())) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect.");
        }
        String token = jwtUtil.genererToken(utilisateur.getEmail());
        return new AuthResponse(token, utilisateur.getNomComplet(), utilisateur.getEmail(), utilisateur.getPlan().name());
    }
}
