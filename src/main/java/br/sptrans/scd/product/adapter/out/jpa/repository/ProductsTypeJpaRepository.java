package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import br.sptrans.scd.product.adapter.out.persistence.entity.ProductTypesEntityJpa;

public interface ProductsTypeJpaRepository extends JpaRepository<ProductTypesEntityJpa, String> , JpaSpecificationExecutor<ProductTypesEntityJpa>{

    Page<ProductTypesEntityJpa> findByCodStatus(String codStatus, Pageable pageable);
    /**
     * Encontra o máximo código tipo produto numérico para auto-incremento.
     * Retorna 0 se nenhum código numérico existir.
     */
    @Query(value = "SELECT COALESCE(MAX(TO_NUMBER(COD_TIPO_PRODUTO)), 0) FROM SPTRANSDBA.TIPOS_PRODUTOS WHERE REGEXP_LIKE(COD_TIPO_PRODUTO, '^[0-9]+$')", nativeQuery = true)
    Long findMaxNumericCode();
}
