package br.sptrans.scd.product.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.Product;

public interface ProductRepository {

    Optional<Product> findById(String codProduto);

    boolean existsByProduct(String codProduto);

    Product save(Product produto);

    void updateStatus(String codProduto, String codStatus, Long idUsuario);

    List<Product> findAll(String codStatus);

    Page<Product> findAll(String codStatus, Pageable pageable);

}
