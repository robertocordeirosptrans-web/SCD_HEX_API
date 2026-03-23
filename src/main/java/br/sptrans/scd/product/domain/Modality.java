package br.sptrans.scd.product.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.domain.enums.DomainStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Modality {

    private String codModalidade;
    private String desModalidade;
    private String codStatus;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private User idUsuarioCadastro;
    private User idUsuarioManutencao;

    public boolean isActive() {
        return DomainStatus.ACTIVE.getCode().equals(this.codStatus);
    }

    public boolean isInactive() {
        return DomainStatus.INACTIVE.getCode().equals(this.codStatus);
    }

    public void activate(User idUsuario) {
        this.codStatus = DomainStatus.ACTIVE.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    public void deactivate(User idUsuario) {
        this.codStatus = DomainStatus.INACTIVE.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }
}
