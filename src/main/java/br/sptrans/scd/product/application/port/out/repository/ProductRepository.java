package br.sptrans.scd.product.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.Product;

public interface ProductRepository {

    Optional<Product> findById(String codProduto);

    boolean existsByProduct(String codProduto);

    Product save(Product produto);

    void updateStatus(String codProduto, String codStatus, Long idUsuario);

    List<Product> findAll(String codStatus);

}
