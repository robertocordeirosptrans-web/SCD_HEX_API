package br.sptrans.scd.product.application.port.in;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.ProductType;

public interface ProductsTypeManagementUseCase {
    ProductType create(CreateProductsTypeCommand command);
    ProductType update(String codTipoProduto, UpdateProductsTypeCommand command);
    void activate(String codTipoProduto, Long idUsuario);
    void inactivate(String codTipoProduto, Long idUsuario);
    void delete(String codTipoProduto);
    Optional<ProductType> findById(String codTipoProduto);
    Page<ProductType> findAll(String codStatus, Pageable pageable);

    // ── Commands ──────────────────────────────────────────────────────────────
    record CreateProductsTypeCommand(
            String codTipoProduto,
            String desTipoProduto,
            Long idUsuario) {}

    record UpdateProductsTypeCommand(
            String desTipoProduto,
            Long idUsuario) {}
}
