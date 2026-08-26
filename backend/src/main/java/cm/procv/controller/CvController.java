package cm.procv.controller;

import cm.procv.entity.Cv;
import cm.procv.entity.Utilisateur;
import cm.procv.repository.UtilisateurRepository;
import cm.procv.service.CvPdfService;
import cm.procv.service.CvService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cv")
@RequiredArgsConstructor
public class CvController {
    private final CvService cvService;
    private final CvPdfService cvPdfService;
    private final UtilisateurRepository utilisateurRepository;

    @PostMapping
    public Cv creer(Authentication auth, @RequestBody Cv cv) { return cvService.creer(auth.getName(), cv); }
    @GetMapping
    public List<Cv> lister(Authentication auth) { return cvService.listerPourUtilisateur(auth.getName()); }
    @GetMapping("/{id}")
    public Cv obtenir(Authentication auth, @PathVariable Long id) { return cvService.obtenirPourUtilisateur(auth.getName(), id); }
    @PutMapping("/{id}")
    public Cv mettreAJour(Authentication auth, @PathVariable Long id, @RequestBody Cv cv) { return cvService.mettreAJour(auth.getName(), id, cv); }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> telechargerPdf(Authentication auth, @PathVariable Long id) {
        Cv cv = cvService.obtenirPourUtilisateur(auth.getName(), id);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(auth.getName()).orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
        byte[] pdf = cvPdfService.genererPdf(cv, utilisateur);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cv.pdf\"").body(pdf);
    }
}
