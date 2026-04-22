
package br.sptrans.scd.product.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import br.sptrans.scd.product.adapter.out.persistence.entity.ProductEntityJpa;
import br.sptrans.scd.product.domain.Product;

public interface ProductPort {
    Optional<Product> findById(String codProduto);

    boolean existsByProduct(String codProduto);

    Product save(Product produto);

    void updateStatus(String codProduto, String codStatus, Long idUsuario);

    List<Product> findAll(String codStatus);


    /**
     * 
     * 
     * Busca paginada com filtros dinâmicos via Specification.
     */
    Page<Product> findAll(Specification<ProductEntityJpa> spec, Pageable pageable);
}
