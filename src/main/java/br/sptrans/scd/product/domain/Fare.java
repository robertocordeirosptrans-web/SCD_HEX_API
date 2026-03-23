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
public class Fare {

    private String codTarifa;

    private String codVersao;

    private LocalDateTime dtVigenciaIni;

    private LocalDateTime dtVigenciaFim;

    private LocalDateTime dtCadastro;

    private LocalDateTime dtManutencao;

    private String desTarifa;

    private String stTarifas;

    private Integer vlTarifa;

    private User idUsuarioCadastro;

    private User idUsuarioManutencao;

    private Product codProduto;

    public boolean isActive() {
        return DomainStatus.ACTIVE.getCode().equals(this.stTarifas);
    }

    public boolean isInactive() {
        return DomainStatus.INACTIVE.getCode().equals(this.stTarifas);
    }

    public void activate(User idUsuario) {
        this.stTarifas = DomainStatus.ACTIVE.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    public void deactivate(User idUsuario) {
        this.stTarifas = DomainStatus.INACTIVE.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

}
