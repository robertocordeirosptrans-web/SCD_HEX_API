package br.sptrans.scd.product.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

import br.sptrans.scd.auth.domain.ClassificationPerson;
import br.sptrans.scd.product.domain.Family;
import br.sptrans.scd.product.domain.Modality;
import br.sptrans.scd.product.domain.Product;
import br.sptrans.scd.product.domain.ProductType;
import br.sptrans.scd.product.domain.ProductVersion;
import br.sptrans.scd.product.domain.Species;
import br.sptrans.scd.product.domain.Technology;

/**
 * Porta de entrada (driven port) do módulo de Produto. Define todos os casos de
 * uso expostos ao mundo externo.
 */
public interface ProductUseCase {
    // =========================================================================
    // Gestão de Produto
    // =========================================================================

    /**
     * Cadastra um novo produto (nasce como INATIVO; versão 1.0 criada
     * automaticamente).
     */
    void createProduct(CreateProductCommand comando);

    /**
     * Ativa o produto. Requer ao menos uma versão com tarifas e regras
     * configuradas.
     */
    void activateProduct(String productCode, Long idUsuario);

    /**
     * Inativa o produto.
     */
    void inactivateProduct(String productCode, Long idUsuario);

    /**
     * Atualiza dados descritivos do produto. COD_PRODUTO é imutável.
     */
    void updateProduct(String productCode, UpdateProductCommand comando);

    /**
     * Busca produto por código.
     */
    Product findByProduct(String codProduto);

    /**
     * Lista todos os produtos, com filtro opcional de status.
     */
    List<Product> findAllProducts(String codStatus);

    // =========================================================================
    // Gestão de Versões
    // =========================================================================
    /**
     * Cria uma nova versão para um produto existente.
     */
    ProductVersion createNewVersion(String codProduto, CreateVersionCommand cmd);

    /**
     * Consulta uma versão específica.
     */
    ProductVersion findByVersion(String codVersao);

    // ── Command / Query records ────────────────────────────────────────────
    record CreateProductCommand(
            String codProduto,
            String desProduto,
            String desEmissorResponsavel,
            String desUtilizacao,
            String flgBloqFabricacao,
            String flgBloqVenda,
            String flgBloqDistribuicao,
            String flgBloqTroca,
            String flgBloqAquisicao,
            String flgBloqPedido,
            String flgBloqDevolucao,
            String flgInicializado,
            String flgComercializado,
            String flgRestManual,
            String codEntidade,
            String codTipoCartao,
            ClassificationPerson codClassificacaoPessoa,
            ProductType codTipoProduto,
            Technology codTecnologia,
            Modality codModalidade,
            Family codFamilia,
            Species codEspecie,
            Long idUsuario
    ) {}

    record UpdateProductCommand(
            String desProduto,
            String desEmissorResponsavel,
            String desUtilizacao,
            String flgBloqFabricacao,
            String flgBloqVenda,
            String flgBloqDistribuicao,
            String flgBloqTroca,
            String flgBloqAquisicao,
            String flgBloqPedido,
            String flgBloqDevolucao,
            String flgInicializado,
            String flgComercializado,
            String flgRestManual,
            String codEntidade,
            String codTipoCartao,
            ClassificationPerson codClassificacaoPessoa,
            ProductType codTipoProduto,
            Technology codTecnologia,
            Modality codModalidade,
            Family codFamilia,
            Species codEspecie,
            Long idUsuario
    ) {}

    record CreateVersionCommand(
            LocalDateTime dtValidade,
            LocalDateTime dtVidaInicio,
            LocalDateTime dtVidaFim,
            LocalDateTime dtLiberacao,
            LocalDateTime dtLancamento,
            LocalDateTime dtVendaInicio,
            LocalDateTime dtVendaFim,
            LocalDateTime dtUsoIni,
            LocalDateTime dtUsoFim,
            LocalDateTime dtTrocaIni,
            LocalDateTime dtTrocaFim,
            String flgBloqFabricacao,
            String flgBloqVenda,
            String flgBloqDistribuicao,
            String flgBloqTroca,
            String flgBloqAquisicao,
            String flgBloqPedido,
            String flgBloqDevolucao,
            String desProdutoVersoes,
            Long idUsuario
    ) {}

}
