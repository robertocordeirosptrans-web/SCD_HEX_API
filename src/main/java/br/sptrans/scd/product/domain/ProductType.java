package br.sptrans.scd.product.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.adapter.in.rest.deserializer.ProductTypeDeserializer;
import br.sptrans.scd.product.domain.enums.ProductDomainStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = ProductTypeDeserializer.class)
public class ProductType implements CatalogueEntity<String> {

    private String codTipoProduto;
    private String desTipoProduto;
    private String codStatus;
    private LocalDateTime dtCadastro;
    private LocalDateTime dtManutencao;
    private User idUsuarioCadastro;
    private User idUsuarioManutencao;

    @Override
    public String getId() {
        return codTipoProduto;
    }

    @Override
    public void setId(String id) {
        this.codTipoProduto = id;
    }

    @Override
    public void setActive(boolean active) {
        this.codStatus = active ? ProductDomainStatus.ACTIVE.getCode() : ProductDomainStatus.INACTIVE.getCode();
    }

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

    public void update(String desTipoProduto, User usuario) {
        this.desTipoProduto = desTipoProduto;
        this.idUsuarioManutencao = usuario;
        this.dtManutencao = LocalDateTime.now();
    }
}
