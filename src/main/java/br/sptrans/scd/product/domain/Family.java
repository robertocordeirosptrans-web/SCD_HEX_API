package br.sptrans.scd.product.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.domain.enums.DomainStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Family {

    private String codFamilia;
    private String desFamilia;
    private String stFamilias;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private User idUsuarioCadastro;
    private User idUsuarioManutencao;

    public boolean isActive() {
        return DomainStatus.ACTIVE.getCode().equals(this.stFamilias);
    }

    public boolean isInactive() {
        return DomainStatus.INACTIVE.getCode().equals(this.stFamilias);
    }

    public void activate(User idUsuario) {
        this.stFamilias = DomainStatus.ACTIVE.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    public void deactivate(User idUsuario) {
        this.stFamilias = DomainStatus.INACTIVE.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }
}
