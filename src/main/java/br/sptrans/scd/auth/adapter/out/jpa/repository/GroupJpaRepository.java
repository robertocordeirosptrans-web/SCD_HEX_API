package br.sptrans.scd.auth.adapter.out.jpa.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.auth.adapter.out.persistence.entity.GroupEntityJpa;


public interface GroupJpaRepository extends JpaRepository<GroupEntityJpa, String>, JpaSpecificationExecutor<GroupEntityJpa> {

	@Query("""
		SELECT g FROM GroupEntityJpa g WHERE g.codGrupo = :codGrupo
	""")
	Optional<GroupEntityJpa> findByCodGrupo(@Param("codGrupo") String codGrupo);

	@Query("""
		SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM GroupEntityJpa g WHERE g.codGrupo = :codGrupo
	""")
	boolean existsByCodGrupo(@Param("codGrupo") String codGrupo);

	@Query("""
		SELECT g FROM GroupEntityJpa g WHERE (:codStatus IS NULL OR g.codStatus = :codStatus)
	""")
	List<GroupEntityJpa> findAllByCodStatus(@Param("codStatus") String codStatus);

	@Query(
		value = "SELECT g FROM GroupEntityJpa g LEFT JOIN FETCH g.usuarioManutencao WHERE (:codStatus IS NULL OR g.codStatus = :codStatus)",
		countQuery = "SELECT COUNT(g) FROM GroupEntityJpa g WHERE (:codStatus IS NULL OR g.codStatus = :codStatus)"
	)
	Page<GroupEntityJpa> findAllByCodStatus(@Param("codStatus") String codStatus, Pageable pageable);

}
