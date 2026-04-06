package br.sptrans.scd.product.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.product.adapter.port.out.persistence.entity.FamilyEntityJpa;

public interface FamilyJpaRepository extends JpaRepository<FamilyEntityJpa, String>, JpaSpecificationExecutor<FamilyEntityJpa> {

	List<FamilyEntityJpa> findByCodStatusOrderByCodFamilia(String codStatus);

	List<FamilyEntityJpa> findAllByOrderByCodFamilia();

	Page<FamilyEntityJpa> findByCodStatus(String codStatus, Pageable pageable);

	@Modifying
	@Transactional
	@Query("UPDATE FamilyEntityJpa f SET f.codStatus = :codStatus, f.dtManutencao = CURRENT_TIMESTAMP, f.idUsuarioManutencao = :idUsuario WHERE f.codFamilia = :codFamilia")
	void updateStatus(String codFamilia, String codStatus, Long idUsuario);
}
