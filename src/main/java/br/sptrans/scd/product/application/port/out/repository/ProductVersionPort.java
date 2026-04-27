package br.sptrans.scd.product.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.ProductVersion;

public interface ProductVersionPort {
    Optional<ProductVersion> findById(String codProduto);
    Optional<ProductVersion> findLastVersion(String codProduto);
    boolean existsByProduct(String codProduto);
    ProductVersion save(ProductVersion produto);
    void updateStatus(String codProduto, String codStatus, Long idUsuario);
    Optional<ProductVersion> findByProduct(String codProduto);
    List<ProductVersion> findAllByProduct(String codProduto);
    Page<ProductVersion> findAllByProduct(String codProduto, Pageable pageable);
}
