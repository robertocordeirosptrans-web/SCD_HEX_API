package br.sptrans.scd.auth.adapter.out.jpa.repository;

import java.util.List;

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

    @Query("SELECT gu FROM GroupUserEntityJpa gu ORDER BY gu.id.codGrupo, gu.id.idUsuario")
    List<GroupUserEntityJpa> findAllGroupUsers();

    @Query("SELECT gu FROM GroupUserEntityJpa gu ORDER BY gu.id.codGrupo, gu.id.idUsuario")
    Page<GroupUserEntityJpa> findAllGroupUsers(Pageable pageable);

}
