package br.sptrans.scd.product.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.sptrans.scd.product.adapter.out.jpa.entity.FamilyEntityJpa;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface FamilyJpaRepository extends JpaRepository<FamilyEntityJpa, String>, JpaSpecificationExecutor<FamilyEntityJpa> {

	List<FamilyEntityJpa> findByCodStatusOrderByCodFamilia(String stFamilias);

	List<FamilyEntityJpa> findAllByOrderByCodFamilia();

	@Modifying
	@Transactional
	@Query("UPDATE FamilyEntityJpa f SET f.stFamilias = :codStatus, f.dtManutencao = CURRENT_TIMESTAMP, f.idUsuarioManutencao = :idUsuario WHERE f.codFamilia = :codFamilia")
	void updateStatus(String codFamilia, String codStatus, Long idUsuario);
}
