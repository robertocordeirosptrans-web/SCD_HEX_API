package br.sptrans.scd.channel.adapter.port.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.channel.adapter.port.out.persistence.entity.TypesActivityEntityJpa;

public interface TypesActivityJpaRepository extends JpaRepository<TypesActivityEntityJpa, String>, JpaSpecificationExecutor<TypesActivityEntityJpa> {

	@Query("SELECT t FROM TypesActivityEntityJpa t WHERE (:codStatus IS NULL OR t.codStatus = :codStatus) ORDER BY t.codAtividade")
	List<TypesActivityEntityJpa> findAllByCodStatus(@Param("codStatus") String codStatus);

	@Modifying
	@Query("UPDATE TypesActivityEntityJpa t SET t.desAtividade = :desAtividade, t.dtManutencao = CURRENT_TIMESTAMP  WHERE t.codAtividade = :codAtividade")
	void updateDescricao(@Param("codAtividade") String codAtividade, @Param("desAtividade") String desAtividade);

	@Modifying
	@Query("UPDATE TypesActivityEntityJpa t SET t.codStatus = :codStatus, t.dtManutencao = CURRENT_TIMESTAMP  WHERE t.codAtividade = :codAtividade")
	void updateStatus(@Param("codAtividade") String codAtividade, @Param("codStatus") String codStatus);

}
