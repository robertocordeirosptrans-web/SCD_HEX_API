package br.sptrans.scd.product.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.ProductType;

public interface ProductsTypePort {
    Optional<ProductType> findById(String codTipoProduto);
    boolean existsById(String codTipoProduto);
    List<ProductType> findAll(String codStatus);
    Page<ProductType> findAll(String codStatus, Pageable pageable);
    ProductType save(ProductType tiposProdutos);
    void updateStatus(String codTipoProduto, String codStatus, Long idUsuario);
    void deleteById(String codTipoProduto);

    /**
     * Encontra o máximo código tipo produto numérico para auto-incremento.
     * Retorna 0 se nenhum código numérico existir.
     */
    Long findMaxNumericCode();
}
