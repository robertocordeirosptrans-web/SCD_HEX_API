package br.sptrans.scd.product.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import br.sptrans.scd.product.adapter.out.persistence.entity.ProductEntityJpa;
import br.sptrans.scd.product.domain.CardType;
import br.sptrans.scd.product.domain.Product;
import br.sptrans.scd.product.domain.ProductVersion;

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
         * Lista todos os produtos, com filtros dinâmicos via Specification.
         */
        Page<Product> findAllProducts(Specification<ProductEntityJpa> spec, Pageable pageable);

        List<CardType> findAllCardTypes();



        // =========================================================================
        // Gestão de Versões
        // =========================================================================
        /**
         * Cria uma nova versão para um produto existente.
         */
        ProductVersion createNewVersion(String codProduto, CreateVersionCommand cmd);

        /**
         * Lista todas as versões de um produto por código de produto.
         */
        List<ProductVersion> findAllVersion(String codProduto);

        /**
         * Lista todas as versões de um produto por código de produto (paginado).
         */
        Page<ProductVersion> findAllVersion(String codProduto, Pageable pageable);

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
                        String codClassificacaoPessoa,
                        String codTipoProduto,
                        String codTecnologia,
                        String codModalidade,
                        String codFamilia,
                        String codEspecie,
                        Long idUsuario) {
        }

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
                        String codClassificacaoPessoa,
                        String codTipoProduto,
                        String codTecnologia,
                        String codModalidade,
                        String codFamilia,
                        String codEspecie,
                        Long idUsuario) {
        }

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
                        Long idUsuario) {
        }

}
