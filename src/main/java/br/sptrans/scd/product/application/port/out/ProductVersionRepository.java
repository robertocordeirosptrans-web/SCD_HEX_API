package br.sptrans.scd.product.application.port.out;

import java.util.Optional;

import br.sptrans.scd.product.domain.ProductVersion;

public interface ProductVersionRepository {

    Optional<ProductVersion> findById(String codProduto);

    Optional<ProductVersion> findLastVersion(String codProduto);

    boolean existsByProduct(String codProduto);

    ProductVersion save(ProductVersion produto);

    void updateStatus(String codProduto, String codStatus, Long idUsuario);

    Optional<ProductVersion> findByProduct(String codProduto);

}
