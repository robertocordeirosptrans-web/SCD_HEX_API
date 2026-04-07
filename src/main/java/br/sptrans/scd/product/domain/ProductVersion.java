package br.sptrans.scd.product.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.product.domain.enums.ProductVersionStatus;
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
public class ProductVersion {

    private String codVersao;

    private String codProduto;

    private LocalDateTime dtValidade;

    private LocalDateTime dtVidaInicio;

    private LocalDateTime dtVidaFim;

    private LocalDateTime dtLiberacao;

    private LocalDateTime dtLancamento;

    private LocalDateTime dtVendaInicio;

    private LocalDateTime dtVendaFim;

    private LocalDateTime dtUsoInicio;

    private LocalDateTime dtUsoFim;

    private LocalDateTime dtTrocaInicio;

    private LocalDateTime dtTrocaFim;

    private String flgBloqFabricacao;

    private String flgBloqVenda;

    private String flgBloqDistribuicao;

    private String flgBloqTroca;

    private String flgBloqAquisicao;

    private String flgBloqPedido;

    private String flgBloqDevolucao;

    private LocalDateTime dtCadastro;

    private LocalDateTime dtManutencao;

    private String codStatus;

    private String desProdutoVersoes;

    private User idUsuarioCadastro;

    private User idUsuarioManutencao;


        // -------------------------------------------------------------------------
    // Consultas de status
    // -------------------------------------------------------------------------

    public boolean isActive() {
        return ProductVersionStatus.ACTIVE.getCode().equals(this.codStatus);
    }

    public boolean isInactive() {
        return ProductVersionStatus.INACTIVE.getCode().equals(this.codStatus);
    }

        // -------------------------------------------------------------------------
    // Transições de status
    // -------------------------------------------------------------------------

    public void activate(User idUsuario) {
        this.codStatus = ProductVersionStatus.ACTIVE.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    public void deactivate(User idUsuario) {
        this.codStatus = ProductVersionStatus.INACTIVE.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }



}
