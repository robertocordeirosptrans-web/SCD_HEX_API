package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import br.sptrans.scd.product.adapter.out.persistence.entity.SpeciesEntityJpa;

public interface SpeciesJpaRepository extends JpaRepository<SpeciesEntityJpa, String> , JpaSpecificationExecutor<SpeciesEntityJpa>{

    Page<SpeciesEntityJpa> findByCodStatus(String codStatus, Pageable pageable);
    /**
     * Encontra o máximo código espécie numérico para auto-incremento.
     * Retorna 0 se nenhum código numérico existir.
     */
    @Query(value = "SELECT COALESCE(MAX(TO_NUMBER(COD_ESPECIE)), 0) FROM SPTRANSDBA.ESPECIES WHERE REGEXP_LIKE(COD_ESPECIE, '^[0-9]+$')", nativeQuery = true)
    Long findMaxNumericCode();
}
