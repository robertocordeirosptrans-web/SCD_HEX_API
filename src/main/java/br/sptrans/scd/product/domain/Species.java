package br.sptrans.scd.product.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.domain.enums.ProductDomainStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Species {

    private String codEspecie;
    private String desEspecie;
    private String codStatus;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private User idUsuarioCadastro;
    private User idUsuarioManutencao;

    public boolean isActive() {
        return ProductDomainStatus.ACTIVE.getCode().equals(this.codStatus);
    }

    public boolean isInactive() {
        return ProductDomainStatus.INACTIVE.getCode().equals(this.codStatus);
    }

    public void activate(User idUsuario) {
        this.codStatus = ProductDomainStatus.ACTIVE.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    public void deactivate(User idUsuario) {
        this.codStatus = ProductDomainStatus.INACTIVE.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    public void update(String desEspecie, User usuario) {
        this.desEspecie = desEspecie;
        this.idUsuarioManutencao = usuario;
        this.dtManutencao = LocalDateTime.now();
    }
}
