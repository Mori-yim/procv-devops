package cm.procv.repository;

import cm.procv.entity.Cv;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CvRepository extends JpaRepository<Cv, Long> {
    List<Cv> findByUtilisateurId(Long utilisateurId);
    long countByUtilisateurId(Long utilisateurId);
}
