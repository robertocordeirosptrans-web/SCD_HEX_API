package br.sptrans.scd.product.application.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.ProductVersion;

interface ProductVersionRepository {

    Optional<ProductVersion> findById(String codProduto);

    boolean existsByProduct(String codProduto);

    ProductVersion save(ProductVersion produto);

    void updateStatus(String codProduto, String codStatus, Long idUsuario);

    List<ProductVersion> findAll(String codStatus);

}
