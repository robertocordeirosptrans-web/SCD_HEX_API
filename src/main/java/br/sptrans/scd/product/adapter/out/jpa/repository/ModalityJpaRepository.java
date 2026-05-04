package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import br.sptrans.scd.product.adapter.out.persistence.entity.ModalityEntityJpa;

public interface ModalityJpaRepository extends JpaRepository<ModalityEntityJpa, String>, JpaSpecificationExecutor<ModalityEntityJpa>{

    Page<ModalityEntityJpa> findByCodStatus(String codStatus, Pageable pageable);

    /**
     * Encontra o máximo código modalidade numérico para auto-incremento.
     * Retorna 0 se nenhum código numérico existir.
     */
    @Query(value = "SELECT COALESCE(MAX(TO_NUMBER(COD_MODALIDADE)), 0) FROM SPTRANSDBA.MODALIDADES WHERE REGEXP_LIKE(COD_MODALIDADE, '^[0-9]+$')", nativeQuery = true)
    Long findMaxNumericCode();
}
