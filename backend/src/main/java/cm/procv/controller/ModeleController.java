package cm.procv.controller;

import cm.procv.dto.ModeleInfo;
import cm.procv.service.ModeleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/modeles")
@RequiredArgsConstructor
public class ModeleController {

    private final ModeleService modeleService;

    @GetMapping
    public List<ModeleInfo> lister() {
        return modeleService.listerModeles();
    }
}
