package br.sptrans.scd.product.domain;

import java.time.LocalDateTime;

import br.sptrans.scd.product.domain.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Entidade de domínio: Produto (tipo de crédito).
 *
 * <p>
 * Um Produto representa um tipo de crédito ofertado pelo SCD — ex: Bilhete
 * Único, Passe Mensal, Crédito Corporativo. Não possui hierarquia.</p>
 *
 * <p>
 * Ciclo de criação obrigatório (4 etapas):
 * <ol>
 * <li>Dados básicos → status INATIVO</li>
 * <li>Versão 1.0 criada automaticamente</li>
 * <li>Tarifas configuradas</li>
 * <li>Regras de bloqueio configuradas → ativação possível</li>
 * </ol>
 * </p>
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private String codProduto;

    private String desProduto;

    private String desEmissorResponsavel;

    private String codStatus;

    private String desUtilizacao;

    private LocalDateTime dtCadastro;

    private LocalDateTime dtManutencao;

    private String flgBloqFabricacao;

    private String flgBloqVenda;

    private String flgBloqDistribuicao;

    private String flgBloqTroca;

    private String flgBloqAquisicao;

    private String flgBloqPedido;

    private String flgBloqDevolucao;

    private String flgInicializado;

    private String flgComercializado;

    private String flgRestManual;

    private String codEntidade;

    private String codTipoCartao;

    private String codClassificacaoPessoa;

    private String codTipoProduto;

    private String codTecnologia;

    private String codModalidade;

    private String codFamilia;

    private String codEspecie;

    private Long idUsuarioCadastro;

    private Long idUsuarioManutencao;

    // -------------------------------------------------------------------------
    // Consultas de status
    // -------------------------------------------------------------------------

    public boolean isActive() {
        return ProductStatus.ACTIVE.getCode().equals(this.codStatus);
    }

    public boolean isInactive() {
        return ProductStatus.INACTIVE.getCode().equals(this.codStatus);
    }

    // -------------------------------------------------------------------------
    // Transições de status
    // -------------------------------------------------------------------------

    public void activate(Long idUsuario) {
        this.codStatus = ProductStatus.ACTIVE.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }

    public void deactivate(Long idUsuario) {
        this.codStatus = ProductStatus.INACTIVE.getCode();
        this.idUsuarioManutencao = idUsuario;
        this.dtManutencao = LocalDateTime.now();
    }
}
