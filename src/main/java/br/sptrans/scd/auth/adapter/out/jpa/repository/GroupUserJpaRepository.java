
package br.sptrans.scd.auth.adapter.out.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sptrans.scd.auth.adapter.out.persistence.entity.GroupUserEntityJpa;
import br.sptrans.scd.auth.adapter.out.persistence.entity.GroupUserEntityJpaId;

public interface GroupUserJpaRepository extends JpaRepository<GroupUserEntityJpa, GroupUserEntityJpaId> {

    @Query("SELECT COUNT(gu) FROM GroupUserEntityJpa gu WHERE gu.id.codGrupo = :codGrupo AND gu.codStatus = 'A'")
    long countActiveUsersByGroup(@Param("codGrupo") String codGrupo);

    @Query(value = "SELECT gu FROM GroupUserEntityJpa gu LEFT JOIN FETCH gu.usuario LEFT JOIN FETCH gu.usuarioManutencao ORDER BY gu.id.codGrupo, gu.id.idUsuario", countQuery = "SELECT COUNT(gu) FROM GroupUserEntityJpa gu")
    Page<GroupUserEntityJpa> findAllGroupUsers(Pageable pageable);

    @Query(value = "SELECT gu FROM GroupUserEntityJpa gu LEFT JOIN FETCH gu.usuario LEFT JOIN FETCH gu.usuarioManutencao WHERE gu.id.idUsuario = :idUsuario ORDER BY gu.id.codGrupo", countQuery = "SELECT COUNT(gu) FROM GroupUserEntityJpa gu WHERE gu.id.idUsuario = :idUsuario")
    Page<GroupUserEntityJpa> findByGroupsUser(@Param("idUsuario") Long idUsuario, Pageable pageable);

    @Query(value = "SELECT gu FROM GroupUserEntityJpa gu LEFT JOIN FETCH gu.usuario LEFT JOIN FETCH gu.usuarioManutencao WHERE gu.id.codGrupo = :codGrupo ORDER BY gu.id.idUsuario", countQuery = "SELECT COUNT(gu) FROM GroupUserEntityJpa gu WHERE gu.id.codGrupo = :codGrupo")
    Page<GroupUserEntityJpa> findByGroupCode(@Param("codGrupo") String codGrupo, Pageable pageable);

}
