package br.sptrans.scd.product.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.ProductType;

/**
 * Output Port para operações de persistência de TiposProdutos.
 */
public interface ProductsTypeRepository {

    Optional<ProductType> findById(String codTipoProduto);

    List<ProductType> findAll();

    ProductType save(ProductType tiposProdutos);
}
