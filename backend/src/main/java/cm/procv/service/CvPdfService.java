package cm.procv.service;

import cm.procv.entity.Cv;
import cm.procv.entity.Utilisateur;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;

/**
 * Genere le PDF d'un CV : le contenu est d'abord rendu en HTML via un
 * template Thymeleaf (templates/cv-template.html), puis converti en PDF.
 * Le plan FREE ajoute un filigrane "ProCV - Version gratuite" ; le plan
 * PREMIUM produit un document sans filigrane.
 */
@Service
@RequiredArgsConstructor
public class CvPdfService {

    private final TemplateEngine templateEngine;

    public byte[] genererPdf(Cv cv, Utilisateur utilisateur) {
        Context contexte = new Context();
        contexte.setVariable("cv", cv);
        contexte.setVariable("filigrane", utilisateur.getPlan() == Utilisateur.Plan.FREE);

        String html = templateEngine.process("cv-template", contexte);

        try (ByteArrayOutputStream sortie = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(sortie);
            builder.run();
            return sortie.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la generation du PDF : " + e.getMessage(), e);
        }
    }
}
