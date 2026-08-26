package cm.procv.service;

import cm.procv.dto.ModeleInfo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Liste des modeles de CV disponibles. Donnee statique en pratique, mais mise
 * en cache local pour illustrer le principe : eviter de recalculer une donnee
 * couteuse ou peu changeante a chaque requete.
 */
@Service
public class ModeleService {

    @Cacheable("modeles-cv")
    public List<ModeleInfo> listerModeles() {
        // Un vrai microservice interrogerait ici une base ou un stockage de
        // templates. On simule un traitement pour rendre le gain du cache visible.
        try { Thread.sleep(150); } catch (InterruptedException ignored) {}

        return List.of(
            new ModeleInfo("CLASSIQUE", "Classique", "Mise en page sobre, adaptee aux candidatures administratives"),
            new ModeleInfo("MODERNE", "Moderne", "Mise en page coloree, adaptee aux profils tech et creatifs"),
            new ModeleInfo("MINIMALISTE", "Minimaliste", "Epuree, met l'accent sur le contenu")
        );
    }
}
