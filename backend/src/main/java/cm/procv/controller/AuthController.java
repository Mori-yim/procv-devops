package cm.procv.controller;

import cm.procv.dto.AuthResponse;
import cm.procv.dto.ConnexionRequest;
import cm.procv.dto.InscriptionRequest;
import cm.procv.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/inscription")
    public AuthResponse inscription(@Valid @RequestBody InscriptionRequest requete) {
        return authService.inscrire(requete);
    }

    @PostMapping("/connexion")
    public AuthResponse connexion(@RequestBody ConnexionRequest requete) {
        return authService.connecter(requete);
    }
}
