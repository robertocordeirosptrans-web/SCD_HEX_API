package br.sptrans.scd.product.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.ProductType;

/**
 * Porta de entrada para gestão de Tipos de Produto.
 */
public interface ProductsTypeManagementUseCase {

    ProductType createProductsType(CreateProductsTypeCommand command);

    ProductType updateProductsType(String codTipoProduto, UpdateProductsTypeCommand command);

    ProductType findByProductsType(String codTipoProduto);

    Page<ProductType> findAllProductsTypes(String codStatus, Pageable pageable);

    void activateProductsType(String codTipoProduto, Long idUsuario);

    void inactivateProductsType(String codTipoProduto, Long idUsuario);

    void deleteProductsType(String codTipoProduto);

    // ── Commands ──────────────────────────────────────────────────────────────

    record CreateProductsTypeCommand(
            String codTipoProduto,
            String desTipoProduto,
            Long idUsuario) {}

    record UpdateProductsTypeCommand(
            String desTipoProduto,
            Long idUsuario) {}
}
