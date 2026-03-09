package br.sptrans.scd.product.application.in;

import br.sptrans.scd.product.domain.Product;

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

    Product activateProduct(String productCode, Long userId);
}
