package br.sptrans.scd.product.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.product.domain.enums.ProductDomainStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fare {

    private String codTarifa;

    private String codVersao;

    private LocalDateTime dtVigenciaInicio;

    private LocalDateTime dtVigenciaFim;

    private LocalDateTime dtCadastro;

    private LocalDateTime dtManutencao;

    private String desTarifa;

    private String codStatus;

    private Integer valTarifa;

    private Long idUsuarioCadastro;

    private Long idUsuarioManutencao;

    private String codProduto;

    public boolean isActive() {
        return ProductDomainStatus.ACTIVE.getCode().equals(this.codStatus);
    }

    public boolean isInactive() {
        return ProductDomainStatus.INACTIVE.getCode().equals(this.codStatus);
    }

    public void activate(Long idUsuario) {
        this.codStatus = ProductDomainStatus.ACTIVE.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    public void deactivate(Long idUsuario) {
        this.codStatus = ProductDomainStatus.INACTIVE.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    public void extendValidity(LocalDateTime dtFim, Long idUsuario) {
        this.dtVigenciaFim = dtFim;
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

}
