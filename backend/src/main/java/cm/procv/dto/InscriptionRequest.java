package cm.procv.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InscriptionRequest {
    @NotBlank private String nomComplet;
    @Email @NotBlank private String email;
    @NotBlank @Size(min = 8, max = 72) private String motDePasse;
    private String telephone;
}
